package com.netty100.broker.processor.service;

import com.netty100.broker.annotation.EnableMessageWay;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.processor.RequestProcessor;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.broker.utils.WhyCountUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 * 客户端消息处理
 */
@EnableMessageWay(value = CommonConstants.way_simplex_channelRead0, executor = CommonConstants.SERVER_EXECUTOR)
@Component
@Slf4j
public class SimplexChannelRead0 implements RequestProcessor {

    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        try {
//            ByteBufferUtil.debugAll(whyMsg.buffer());

            WhyCountUtils.platform_s2p_message_read_success_total.add(1);
            WhyCountUtils.platform_s2p_message_read_success_flow.add(whyMsg.length());

            whyMsg.getFixedHeader().setMessageId(WhyMessageFactory.createMessageId());
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M31.getCode(), whyMsg, LogPointCode.M31.getMessage() + "<" + ctx.channel().remoteAddress() + ">", 0);

            //消息响应
            WhyChannelUtils.p2sSimplexWriteAndFlush(ctx, whyMsg, kernelConfig.getServerCacheChannelReTimes());
        } catch (Exception e) {
            log.error("单工消息消费失败", e);
            WhyCountUtils.platform_s2p_message_read_fail_total.add(1);
            WhyCountUtils.platform_s2p_message_read_fail_flow.add(whyMsg.length());
            WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(whyMsg, ResponseCode.Rep203.getCodeBytes()));
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M34.getCode(), whyMsg, LogPointCode.M34.getMessage() + SysUtility.getErrorMsg(e));
        }
    }
}
