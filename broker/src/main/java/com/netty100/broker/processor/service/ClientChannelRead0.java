package com.netty100.broker.processor.service;

import com.netty100.broker.annotation.EnableMessageWay;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.broker.processor.RequestProcessor;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.broker.utils.WhyCountUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 * 客户端消息处理
 */
@EnableMessageWay(value = CommonConstants.way_c2p_channelRead0, executor = CommonConstants.CLIENT_EXECUTOR)
@Component
@Slf4j
public class ClientChannelRead0 implements RequestProcessor {
    private WhyKernelProperties whyKernelProperties;


    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        try {
//            ByteBufferUtil.debugAll(whyMsg.buffer());
            WhyCountUtils.platform_c2p_message_read_success_total.add(1);
            WhyCountUtils.platform_c2p_message_read_success_flow.add(whyMsg.length());

            String channelKey = WhyChannelUtils.getCurrentC2pChannelKey(whyMsg);

            //重写客户端的MessageId，防止客户端一直传入重复的Id
            whyMsg.getFixedHeader().setMessageId(WhyMessageFactory.createMessageId());
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M01.getCode(), whyMsg, LogPointCode.M01.getMessage() + "<" + ctx.channel().remoteAddress() + ">", 0);

            //没有开启云端连接功能，不做用户注册码与配置校验
            if(SysUtility.isEmpty(whyKernelProperties)){
                whyKernelProperties = WhySpringUtils.getBean(WhyKernelProperties.class);
            }
            if(!SysUtility.isEmpty(whyKernelProperties.getDomain())){
                final String configStr = WhyChannelUtils.getCurrentS2pChannelKey(whyMsg);
                if(!WhyCloudUtils.serviceConfigs.contains(configStr)){
                    WhyMessage whyMessage = WhyMessageFactory.newMessage(whyMsg, ResponseCode.Rep114.getCodeBytes());
                    whyMessage.getFixedHeader().setMessageWay(CommonConstants.way_c2p_channelActive);
                    WhyChannelUtils.writeAndFlush(ctx, whyMessage);
                    WhyChannelUtils.c2pChannelForceClose(whyMsg, channelKey, ctx, ResponseCode.Rep114.getCodeBytes(), LogPointCode.C05.getCode(), LogPointCode.C05.getMessage());
                    return;
                }

                String c2pChannelKey = WhyChannelUtils.getCurrentC2pChannelKey(whyMsg);
                if(!WhyChannelUtils.getC2pCacheChannelMap().containsKey(c2pChannelKey)){
                    WhyMessage whyMessage = WhyMessageFactory.newMessage(whyMsg, ResponseCode.Rep107.getCodeBytes());
                    whyMessage.getFixedHeader().setMessageWay(CommonConstants.way_c2p_channelActive);
                    WhyChannelUtils.writeAndFlush(ctx, whyMessage);
                    WhyChannelUtils.c2pChannelForceClose(whyMsg, channelKey, ctx, ResponseCode.Rep107.getCodeBytes(), LogPointCode.C06.getCode(), LogPointCode.C06.getMessage());
                    return;
                }
            }
            WhyChannelUtils.p2sWriteAndFlush(ctx, whyMsg, kernelConfig.getServerCacheChannelReTimes());
        } catch (Exception e) {
            log.error("客户端消息消费失败", e);
            WhyCountUtils.platform_c2p_message_read_fail_total.add(1);
            WhyCountUtils.platform_c2p_message_read_fail_flow.add(whyMsg.length());
            WhyMessage whyMessage = WhyMessageFactory.newMessage(whyMsg, ResponseCode.Rep103.getCodeBytes());
            whyMessage.getFixedHeader().setMessageWay(CommonConstants.way_c2p_channelActive);
            WhyChannelUtils.writeAndFlush(ctx, whyMessage);
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M04.getCode(), whyMsg, LogPointCode.M04.getMessage() + SysUtility.getErrorMsg(e));
        }
    }
}
