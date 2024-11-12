package com.netty100.netty.client.channel;

import com.netty100.netty.client.utils.WhyServerUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
@ChannelHandler.Sharable
public class WhyServerIdleHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && IdleState.WRITER_IDLE == ((IdleStateEvent) evt).state()) {
            WhyServerUtils.closeCtx(ctx.channel());
            log.error("sdk管道心跳断开{}（90秒内未发送过数据）", ctx.channel().id());
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

}



