package com.netty100.kernel.processor.temporary;

import com.netty100.kernel.annotation.EnableMessageWay;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.processor.RequestProcessor;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.kernel.utils.WhyChannelUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 * 不支持的消息解析处理
 */
@EnableMessageWay(value = CommonConstants.way_default_channelActive, executor = CommonConstants.CONNECT_EXECUTOR)
@Component
public class DefaultChannelActive implements RequestProcessor {

    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        try {
            WhyChannelUtils.writeAndFlush(ctx, WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep903.getCodeBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
