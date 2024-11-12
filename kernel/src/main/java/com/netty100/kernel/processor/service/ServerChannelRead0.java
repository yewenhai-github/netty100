package com.netty100.kernel.processor.service;

import com.netty100.kernel.annotation.EnableMessageWay;
import com.netty100.kernel.autoconfig.WhyKernelContainer;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.devops.queue.WhyMessageQueue;
import com.netty100.kernel.processor.RequestProcessor;
import com.netty100.kernel.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.kernel.utils.WhyChannelUtils;
import com.netty100.kernel.utils.WhyCountUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageCode;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/28
 * @since 1.0.0, 2022/4/28
 * 服务器消息处理（web、job、mq）
 */
@Slf4j
@EnableMessageWay(value = CommonConstants.way_s2p_channelRead0, executor = CommonConstants.SERVER_EXECUTOR)
@Component
public class ServerChannelRead0 implements RequestProcessor {

    @Override
    public void doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyKernelProperties kernelConfig, WhyNettyRemoting remotingClient) {
        try {
            WhyCountUtils.platform_s2p_message_read_success_total.add(1);
            WhyCountUtils.platform_s2p_message_read_success_flow.add(topeMsg.length());

//            ByteBufferUtil.debugAll(topeMsg.buffer());

            //只有sdk触发的消息才需要区分叶子，sdk中job、mq这类主动发起生成的消息，属于第一次消息  0主节点 1叶子节点
            if(WhyMessageCode.type_online_single_message.getCode() == topeMsg.getFixedHeader().getMessageType()
                || WhyMessageCode.type_online_all_message.getCode() == topeMsg.getFixedHeader().getMessageType()){
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M31.getCode(), topeMsg, LogPointCode.M31.getMessage() + "<" + ctx.channel().remoteAddress() + ">", topeMsg.getVariableHeader().isLeaf() ? 0 : 1);
            }

            /***
             * 消息类型标志位（1字节）
             * 0平台接收
             * 1在线单用户，本地消息直接发送
             * 2在线全用户，本地消息直接发送,触发广播发送方式：循环本机channelMap+内部转发
             * 3离线单用户，暂不实现
             * 4离线全用户，暂不实现
             * 5在线全用户广播消费（和2配套使用）
             * 6在线单用户-定点转发（和1配套使用，如果定点发送失败，需清除错误的redis地址，并丢弃本次消息）
             * 7在线单用户-群发转发（和1配套使用，redis找不到addr，群发后只有消费成功的channel，将对应的addr记录到redis中）
             * 8平台客户端消息踢人下线内部报文
             */
            WhyKernelContainer.loadHandler(topeMsg.getFixedHeader().getMessageType()).doCommand(ctx, topeMsg, remotingClient, kernelConfig);
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("服务器消息处理失败：{}", e);
            WhyCountUtils.platform_s2p_message_read_fail_total.add(1);
            WhyCountUtils.platform_s2p_message_read_fail_flow.add(topeMsg.length());
            WhyChannelUtils.writeAndFlush(ctx, WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep203.getCodeBytes()));
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M34.getCode(), topeMsg, LogPointCode.M34.getMessage() + SysUtility.getErrorMsg(e));
        }
    }

}
