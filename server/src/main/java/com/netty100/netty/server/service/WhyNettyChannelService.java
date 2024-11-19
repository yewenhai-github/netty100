package com.netty100.netty.server.service;

import com.netty100.common.service.TopeChannelService;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public interface WhyNettyChannelService<I> extends TopeChannelService<I> {

    /**
     * 用户上线
     * @param userId
     */
    default void channelActive(Long userId, ChannelHandlerContext ctx){

    }

    /**
     * 用户主动下线
     * @param userId
     */
    default void channelInactive(Long userId, ChannelHandlerContext ctx){

    }

    /**
     * 用户心跳下线
     * @param ctx
     */
    default void channelIdleInactive(Long userId, ChannelHandlerContext ctx){

    }

    /**
     * 系统异常导致用户下线
     * @param userId
     */
    default void exceptionCaught(Long userId, ChannelHandlerContext ctx){

    }

}
