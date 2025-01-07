package com.netty100.broker.processor.service;

import com.netty100.broker.annotation.EnableMessageWay;
import com.netty100.broker.devops.queue.WhyConnectQueue;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.broker.processor.RequestProcessor;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 * 游戏端心跳响应
 */
@EnableMessageWay(value = CommonConstants.way_c2p_idleState, executor = CommonConstants.PING_EXECUTOR)
@Component
@Slf4j
public class ClientIdleState implements RequestProcessor {

    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        try {
            //校验
            String configStr = WhyChannelUtils.getCurrentS2pChannelKey(whyMsg);
            if(!WhyCloudUtils.serviceConfigs.contains(configStr)){
                log.info("客户端配置失败(3)，消息协议未配置，用户{}，设备Id{}", whyMsg.getFixedHeader().getUserId(), whyMsg.getFixedHeader().getDeviceId());
                WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(whyMsg, ResponseCode.Rep114.getCodeBytes()));
                return;
            }

            WhyChannelUtils.writeAndFlush(ctx, WhyMessageFactory.newMessage(whyMsg, ResponseCode.Rep102.getCodeBytes()));
            WhyConnectQueue.pushClientChannelIdleActiveQueue(ctx, whyMsg.getFixedHeader(), WhyChannelUtils.getCurrentC2pChannelKey(whyMsg));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
