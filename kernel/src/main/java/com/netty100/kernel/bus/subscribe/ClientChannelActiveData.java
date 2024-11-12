package com.netty100.kernel.bus.subscribe;

import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.common.protocol.WhyMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

@Data
public class ClientChannelActiveData {
    private WhyMessage whyMessage;
    private ChannelHandlerContext ctx;
    private WhyKernelProperties kernelConfig;
    private WhyNettyRemoting remotingClient;
}
