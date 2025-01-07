package com.netty100.broker.processor.service.server;

import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author why
 * @version 1.0.0, 2022/4/1
 * @since 1.0.0, 2022/4/1
 */
public interface MessageTypeService {

    default boolean doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyNettyRemoting remotingClient, WhyKernelProperties kernelConfig){
        if(SysUtility.isEmpty(remotingClient) || SysUtility.isEmpty(kernelConfig)){
            return false;
        }

        try {
            String channelKey = WhyChannelUtils.getCurrentC2pChannelKey(whyMsg);
            Channel channel = WhyChannelUtils.getChannel(channelKey);
            if (channelKey == null || SysUtility.isEmpty(channel)) {
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M33.getCode(), whyMsg, LogPointCode.M33.getMessage()+"[channelKey="+channelKey+"]");
                return false;
            }
            ChannelFuture future = channel.writeAndFlush(WhyMessageFactory.getClientWhyMessage(whyMsg).bytes());
            future.addListener((ChannelFutureListener) future1 -> {
                if (!future1.isSuccess()) {
                    WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M34.getCode(), whyMsg, LogPointCode.M34.getMessage() + "回调监听触发");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("用户{},消息发送失败{}", whyMsg.getFixedHeader().getUserId(), e);
            return false;
        }
        return true;
    }

}
