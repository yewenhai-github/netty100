package com.netty100.broker.processor.service;

import com.netty100.broker.annotation.EnableMessageWay;
import com.netty100.broker.devops.queue.WhyConnectQueue;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.processor.RequestProcessor;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 * 服务器心跳响应
 */
@EnableMessageWay(value = CommonConstants.way_s2p_idleState, executor = CommonConstants.PING_EXECUTOR)
@Component
public class ServerIdleState implements RequestProcessor {

    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        try {
            WhyChannelUtils.writeAndFlush(ctx, WhyMessageFactory.newMessage(whyMsg, ResponseCode.Rep202.getCodeBytes()));
            WhyConnectQueue.pushServerChannelIdleActiveQueue(ctx, whyMsg.getFixedHeader(), WhyChannelUtils.getCurrentS2pChannelKey(whyMsg));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
