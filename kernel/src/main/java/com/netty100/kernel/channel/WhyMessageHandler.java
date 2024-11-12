package com.netty100.kernel.channel;

import cn.hutool.core.lang.Assert;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.autoconfig.WhyKernelContainer;
import com.netty100.kernel.processor.RequestPair;
import com.netty100.kernel.processor.RequestProcessor;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
@ChannelHandler.Sharable
public class WhyMessageHandler extends SimpleChannelInboundHandler {
    private WhyNettyRemoting remotingClient;
    private WhyKernelProperties whyKernelProperties;

    public WhyMessageHandler(WhyNettyRemoting whyNettyRemoting, WhyKernelProperties whyKernelProperties){
        this.remotingClient = whyNettyRemoting;
        this.whyKernelProperties = whyKernelProperties;
    }

    /**
     * 客户端消息解析
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try {
            //1. 校验
            Assert.notNull(msg, "channelRead0消息不能为空..");
            Assert.isTrue(channelIsActive(ctx));
            //2. 校验管道是否可用
            WhyMessage topeMsg = (WhyMessage)msg;
            //3. 业务逻辑处理，具体的消息消费逻辑
            byte messageWay = topeMsg.getFixedHeader().getMessageWay();
            RequestPair requestPair = WhyKernelContainer.loadRequestPair(messageWay);
            if(SysUtility.isEmpty(requestPair)){
                log.error("未知报文无法解析，数据来源{}-{}，报文类型{}", topeMsg.getFixedHeader().getMessageSource(), topeMsg.getFixedHeader().getMessageDest(), messageWay);
                ctx.fireChannelRead(msg);
                return;
            }
            RequestProcessor requestProcessor = (RequestProcessor)requestPair.getRequestProcessor();
            ExecutorService executorService = (ExecutorService)requestPair.getExecutorService();
            //4. 生成一个Runnable任务
            Runnable runnable = () -> requestProcessor.doCommand(ctx, topeMsg, whyKernelProperties, remotingClient);
            //5. 提交到执行器处理
            executorService.submit(runnable);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.error("Reject why request, cause={}", e.getMessage());
        }

    }

    private boolean channelIsActive(ChannelHandlerContext ctx){
        if(ctx.channel().isActive()){
            return true;
        }
        //直接关闭此管道，让其触发channelInactive(ChannelHandlerContext ctx)来清除内核对应的channel缓存
        ctx.close();
        return false;
    }

}