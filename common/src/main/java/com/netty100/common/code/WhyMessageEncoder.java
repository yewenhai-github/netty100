
package com.netty100.common.code;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.commons.lang.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Sharable
public class WhyMessageEncoder extends MessageToMessageEncoder<byte[]> {

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        byte[] header = ByteBuffer.allocate(4).putInt(msg.length).array();
        byte[] result = ArrayUtils.addAll(header, msg);
        out.add(Unpooled.wrappedBuffer(result));
    }
}
