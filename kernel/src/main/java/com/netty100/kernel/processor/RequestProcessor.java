package com.netty100.kernel.processor;

import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.common.protocol.WhyMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 */
public interface RequestProcessor {

    default void doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {

    }

}
