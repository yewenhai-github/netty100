package com.netty100.broker.processor.service;

import com.netty100.broker.annotation.EnableMessageWay;
import com.netty100.broker.devops.queue.WhyConnectQueue;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.processor.RequestProcessor;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.broker.utils.WhyCountUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 * 服务器连接处理（job）
 */
@EnableMessageWay(value = CommonConstants.way_simplex_channelActive, executor = CommonConstants.CONNECT_EXECUTOR)
@Component
public class SimplexChannelActive implements RequestProcessor {

    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        try {
            String clusterKeySimplex = WhyChannelUtils.getCurrentS2pChannelKeySimplex(topeMsg);
            //缓存客户端连接的Channel
            WhyChannelUtils.addS2pCacheChannel(clusterKeySimplex, WhyChannelUtils.simplexChannelType, ctx);
            //消息响应
            WhyChannelUtils.writeAndFlush(ctx, WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep201.getCodeBytes()));
            //统计信息收集
            WhyCountUtils.platform_s2p_connect_active_total.add(1);
            WhyConnectQueue.pushServerChannelActiveQueue(ctx, topeMsg.getFixedHeader(), clusterKeySimplex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}