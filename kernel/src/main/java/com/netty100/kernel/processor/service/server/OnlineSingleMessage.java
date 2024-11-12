package com.netty100.kernel.processor.service.server;

import com.netty100.kernel.annotation.EnableMessageType;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.devops.queue.WhyMessageQueue;
import com.netty100.kernel.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/1
 * @since 1.0.0, 2022/4/1
 */
@Slf4j
@Component
@EnableMessageType(value = CommonConstants.type_online_single_message)
public class OnlineSingleMessage implements MessageTypeService {
    @Autowired
    private OnlineRelayMessage onlineRelayMessage;

    @Override
    public boolean doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyNettyRemoting remotingClient, WhyKernelProperties kernelConfig) {
        boolean send = MessageTypeService.super.doCommand(ctx, topeMsg, remotingClient, kernelConfig);
        if(send){
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M32.getCode(), topeMsg, LogPointCode.M32.getMessage());
        }else {
            onlineRelayMessage.doRelayProducer(ctx, topeMsg, remotingClient);
        }
        return send;
    }


}
