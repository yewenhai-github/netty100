package com.netty100.broker.channel;

import cn.hutool.core.lang.Assert;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.autoconfig.WhyKernelContainer;
import com.netty100.broker.processor.RequestPair;
import com.netty100.broker.processor.RequestProcessor;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * @author why
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
            WhyMessage whyMsg = (WhyMessage)msg;
            //3. 业务逻辑处理，具体的消息消费逻辑
            byte messageWay = whyMsg.getFixedHeader().getMessageWay();
            RequestPair requestPair = WhyKernelContainer.loadRequestPair(messageWay);
            if(SysUtility.isEmpty(requestPair)){
                log.error("未知报文无法解析，数据来源{}-{}，报文类型{}", whyMsg.getFixedHeader().getMessageSource(), whyMsg.getFixedHeader().getMessageDest(), messageWay);
                ctx.fireChannelRead(msg);
                return;
            }
            RequestProcessor requestProcessor = (RequestProcessor)requestPair.getRequestProcessor();
            ExecutorService executorService = (ExecutorService)requestPair.getExecutorService();
            //4. 生成一个Runnable任务
            Runnable runnable = () -> requestProcessor.doCommand(ctx, whyMsg, whyKernelProperties, remotingClient);
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