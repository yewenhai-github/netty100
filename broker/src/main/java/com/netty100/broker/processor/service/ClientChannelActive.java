package com.netty100.broker.processor.service;

import com.netty100.broker.annotation.EnableMessageWay;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.bus.EventBusUtils;
import com.netty100.broker.bus.subscribe.ClientChannelActiveData;
import com.netty100.broker.processor.RequestProcessor;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 * 游戏端连接处理
 */
@EnableMessageWay(value = CommonConstants.way_c2p_channelActive, executor = CommonConstants.CONNECT_EXECUTOR)
@Component
@Slf4j
public class ClientChannelActive implements RequestProcessor {
    @Autowired
    EventBusUtils eventBusUtils;
    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        // 发送事件消息
        ClientChannelActiveData event = new ClientChannelActiveData();
        event.setCtx(ctx);
        event.setWhyMessage(whyMsg);
        event.setKernelConfig(kernelConfig);
        event.setRemotingClient(remotingClient);
        eventBusUtils.post(event);
    }

}
