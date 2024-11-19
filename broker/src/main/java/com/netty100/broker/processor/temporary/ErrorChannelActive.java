package com.netty100.broker.processor.temporary;

import com.netty100.broker.annotation.EnableMessageWay;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.processor.RequestProcessor;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.common.protocol.ResponseCode;
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
 * 不支持的消息解析处理
 */
@Slf4j
@EnableMessageWay(value = CommonConstants.way_error_channelActive, executor = CommonConstants.CONNECT_EXECUTOR)
@Component
public class ErrorChannelActive implements RequestProcessor {

    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        try {
            String content = ResponseCode.Rep900.getCode()+"-"+ ResponseCode.Rep900.getMassage() ;
            WhyMessage whyMessage = WhyMessageFactory.newMessage(topeMsg, content.getBytes());
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M99.getCode(), topeMsg, LogPointCode.M99.getMessage() /*+ SysUtility.getErrorMsg(e)*/);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
