package com.netty100.common.code;

import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
public class WhyMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        //前4个字节不完整，直接返回
        if (buffer.readableBytes() < 4) {
            return;
        }

        buffer.markReaderIndex();
        int dataLength = buffer.readInt();
        //可读的指针 少于 包的长度，说明没有数据传完
        if (dataLength <= 0 || buffer.readableBytes() < dataLength) {
            buffer.resetReaderIndex();
            return;
        }

        //解码器
        try {
            out.add(WhyMessageFactory.decode(buffer, dataLength));
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("消息解码失败{}", e.getCause().getMessage());
            e.printStackTrace();
            WhyMessage message = WhyMessageFactory.newMessage(new WhyMessage(), "901".getBytes("UTF-8"));
            ctx.writeAndFlush(message.bytes());
            ctx.close();
        }
    }

}
