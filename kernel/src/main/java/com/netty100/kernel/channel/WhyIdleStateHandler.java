package com.netty100.kernel.channel;

import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.devops.queue.WhyConnectQueue;
import com.netty100.kernel.protocol.LogPointCode;
import com.netty100.kernel.utils.WhyChannelUtils;
import com.netty100.kernel.utils.WhyCountUtils;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageCode;
import com.netty100.common.protocol.WhyMessageFactory;
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
public class WhyIdleStateHandler extends ChannelDuplexHandler {
    private WhyKernelProperties whyKernelProperties;

    public WhyIdleStateHandler(WhyKernelProperties whyKernelProperties){
        this.whyKernelProperties = whyKernelProperties;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && IdleState.READER_IDLE == ((IdleStateEvent) evt).state()) {
            String channelKey = WhyChannelUtils.getChannelKeyByCtx(ctx);
            String channelType = WhyChannelUtils.getS2pChannelType(ctx);
            try {
                //客户端管道处理
                if(WhyChannelUtils.c2pChannelCacheDelete(ctx, channelKey)){
                    String[] keys = channelKey.split(IpPortBasedClient.ID_DELIMITER);
                    Long userId = Long.valueOf(keys[0]);

                    WhyMessage topeMsg = WhyMessageFactory.newDefaultMessage(WhyMessageCode.way_c2p_idleState.getCode(), userId, ResponseCode.Rep106.getCodeBytes(), whyKernelProperties.isVariableFlag());
                    topeMsg.getFixedHeader().setUserId(userId);
                    topeMsg.getFixedHeader().setMessageSource(Byte.valueOf(keys[1]));
                    topeMsg.getFixedHeader().setMessageDest(Byte.valueOf(keys[2]));

                    //通知到客户端
                    WhyChannelUtils.writeAndFlush(ctx, topeMsg);
                    //推送到服务器
                    WhyChannelUtils.p2sWriteAndFlush(ctx, topeMsg, whyKernelProperties.getServerCacheChannelReTimes());

                    //客户端管道关闭成功
                    WhyCountUtils.platform_c2p_connect_idle_close_total.add(1);
                    WhyConnectQueue.pushClientChannelIdleInactiveQueue(ctx, userId, channelKey);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, topeMsg, LogPointCode.C04.getCode(), LogPointCode.C04.getMessage());
                    return;
                }

                //服务器管道处理
                if(WhyChannelUtils.s2pChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                    WhyCountUtils.platform_s2p_connect_idle_close_total.add(1);
                    WhyConnectQueue.pushServerChannelIdleInactiveQueue(ctx, channelType);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C57.getCode(), LogPointCode.C57.getMessage());
                }else if(WhyChannelUtils.s2pJobChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                    WhyCountUtils.platform_s2p_connect_idle_close_total.add(1);
                    WhyConnectQueue.pushServerChannelIdleInactiveQueue(ctx, channelType);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C58.getCode(), LogPointCode.C58.getMessage());
                }else if(WhyChannelUtils.s2pMqChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                    WhyCountUtils.platform_s2p_connect_idle_close_total.add(1);
                    WhyConnectQueue.pushServerChannelIdleInactiveQueue(ctx, channelType);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C59.getCode(), LogPointCode.C59.getMessage());
                }else if(WhyChannelUtils.simplexChannelType.equalsIgnoreCase(channelType) && WhyChannelUtils.s2pChannelCacheDelete(ctx)){
                    WhyCountUtils.platform_s2p_connect_idle_close_total.add(1);
                    WhyConnectQueue.pushServerChannelIdleInactiveQueue(ctx, channelType);
                    WhyConnectQueue.pushClientChannelLogQueue(ctx, null, LogPointCode.C62.getCode(), LogPointCode.C62.getMessage());
                }
            } catch (Exception e) {
                log.error("ctx close error userEventTriggered,channelKey={},channelType={},channelId={}", channelKey, channelType, ctx.channel().id(), e);
            } finally {
                ctx.close();
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }
}