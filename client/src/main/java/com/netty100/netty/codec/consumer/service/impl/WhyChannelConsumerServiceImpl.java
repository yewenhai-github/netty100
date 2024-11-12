package com.netty100.netty.codec.consumer.service.impl;

import com.netty100.netty.client.service.WhyNettyChannelService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>Description:</p>
 * <p>Copyright: Copyright (c)2018</p>
 * <p>Company: why</p>
 * <P>Created Date :2021-11-21</P>
 * <P>@version 1.0</P>
 */
@Slf4j
@Service
public class WhyChannelConsumerServiceImpl implements WhyNettyChannelService<byte[]> {

    @Override
    public void channelActive(Long userId, ChannelHandlerContext ctx) {
        log.info("请实现接口方法channelActive，用户{}登录成功...", userId);
    }

    @Override
    public void channelInactive(Long userId, ChannelHandlerContext ctx) {
        log.info("请实现接口方法channelInactive，用户{}退出登录...", userId);
    }

    @Override
    public void exceptionCaught(Long userId, ChannelHandlerContext ctx) {
        log.info("请实现接口方法exceptionCaught，用户{}断线退出...", userId);
    }

}
