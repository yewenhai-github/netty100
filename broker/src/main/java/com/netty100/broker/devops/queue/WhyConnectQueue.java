package com.netty100.broker.devops.queue;

import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.entity.*;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFixedHeader;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author why
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
public class WhyConnectQueue {
    private static WhyKernelProperties whyKernelProperties;
    /*****
     * 游戏端连接、正常断开、异常断开、心跳断开
     * ****/
    public LinkedBlockingQueue<Object> clientChannelActiveQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> clientChannelInactiveQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> clientChannelExceptionCaughtQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> clientChannelIdleInactiveQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> clientChannelIdleActiveQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> clientChannelLogQueue = new LinkedBlockingQueue<>();
    /*****
     * 服务器连接、正常断开、异常断开
     * ****/
    public LinkedBlockingQueue<Object> serverChannelActiveQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> serverChannelInactiveQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> serverChannelExceptionCaughtQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> serverChannelIdleInactiveQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<Object> serverChannelIdleActiveQueue = new LinkedBlockingQueue<>();

    private static WhyConnectQueue instance = new WhyConnectQueue();
    public static WhyConnectQueue getInstance(){
        if(SysUtility.isEmpty(whyKernelProperties)){
            whyKernelProperties = WhySpringUtils.getBean(WhyKernelProperties.class);
        }
        return instance;
    }

    public static void pushClientChannelActiveQueue(ClientChannelActive entity){
        getInstance().clientChannelActiveQueue.offer(entity);
    }

    public static void pushClientChannelInactiveQueue(ClientChannelInactive entity){
        getInstance().clientChannelInactiveQueue.offer(entity);
    }

    public static void pushClientChannelExceptionCaughtQueue(ClientChannelExceptionCaught entity){
        getInstance().clientChannelExceptionCaughtQueue.offer(entity);
    }

    public static void pushClientChannelIdleInactiveQueue(ClientChannelIdleInactive entity){
        getInstance().clientChannelIdleInactiveQueue.offer(entity);
    }

    public static void pushClientChannelIdleActiveQueue(ClientChannelIdleActive entity){
        getInstance().clientChannelIdleActiveQueue.offer(entity);
    }

    public static void pushClientChannelLogQueue(ClientChannelLog entity){
        getInstance().clientChannelLogQueue.offer(entity);
    }

    public static void pushServerChannelActiveQueue(ServerChannelActive entity){
        getInstance().serverChannelActiveQueue.offer(entity);
    }

    public static void pushServerChannelInactiveQueue(ServerChannelInactive entity){
        getInstance().serverChannelInactiveQueue.offer(entity);
    }

    public static void pushServerChannelExceptionCaughtQueue(ServerChannelExceptionCaught entity){
        getInstance().serverChannelExceptionCaughtQueue.offer(entity);
    }

    public static void pushServerChannelIdleInactiveQueue(ServerChannelInactive entity){
        getInstance().serverChannelIdleInactiveQueue.offer(entity);
    }

    public static void pushServerChannelIdleActiveQueue(ServerChannelIdleActive entity){
        getInstance().serverChannelIdleActiveQueue.offer(entity);
    }

    public static void pushClientChannelActiveQueue(ChannelHandlerContext ctx, WhyMessageFixedHeader hearders, String channelKey){
        ClientChannelActive entity = new ClientChannelActive();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setConnectTime(new Date());
        entity.setRemoteIp(SysUtility.getCtxRemoteIp(ctx));
        entity.setDeviceId(hearders.getDeviceId());
        entity.setUserId(hearders.getUserId());
        entity.setMessageWay(hearders.getMessageWay());
        entity.setMessageType(hearders.getMessageType());
        entity.setMessageSource(hearders.getMessageSource());
        entity.setMessageDest(hearders.getMessageDest());
        entity.setMessageSerialize(hearders.getMessageSerialize());
        pushClientChannelActiveQueue(entity);
    }

    public static void pushClientChannelInactiveQueue(ChannelHandlerContext ctx, Long userId, String channelKey){
        ClientChannelInactive entity = new ClientChannelInactive();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setDisConnectTime(new Date());
        entity.setUserId(userId);
        pushClientChannelInactiveQueue(entity);
    }

    public static void pushClientChannelExceptionCaughtQueue(ChannelHandlerContext ctx, Long userId, String channelKey){
        ClientChannelExceptionCaught entity = new ClientChannelExceptionCaught();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setDisConnectTime(new Date());
        entity.setUserId(userId);
        pushClientChannelExceptionCaughtQueue(entity);
    }

    public static void pushClientChannelIdleInactiveQueue(ChannelHandlerContext ctx, Long userId, String channelKey){
        ClientChannelIdleInactive entity = new ClientChannelIdleInactive();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setDisConnectTime(new Date());
        entity.setUserId(userId);
        pushClientChannelIdleInactiveQueue(entity);
    }

    public static void pushClientChannelIdleActiveQueue(ChannelHandlerContext ctx, WhyMessageFixedHeader hearders, String channelKey){
        String[] addrs = SysUtility.getChannelAddr(ctx);

        ClientChannelIdleActive entity = new ClientChannelIdleActive();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setIdleTime(new Date());
        entity.setLocalAddress(addrs[0]);
        entity.setLocalPort(addrs[1]);
        entity.setRemoteAddress(addrs[2]);
        entity.setRemotePort(addrs[3]);
        entity.setDeviceId(hearders.getDeviceId());
        entity.setUserId(hearders.getUserId());
        entity.setMessageWay(hearders.getMessageWay());
        entity.setMessageType(hearders.getMessageType());
        entity.setMessageSource(hearders.getMessageSource());
        entity.setMessageDest(hearders.getMessageDest());
        entity.setMessageSerialize(hearders.getMessageSerialize());
        pushClientChannelIdleActiveQueue(entity);
//        WheelTimerUtils.addClientChannelIdleActiveTask(topeKernelProperties, false);
    }

    public static void pushClientChannelLogQueue(ChannelHandlerContext ctx, WhyMessage topeMsg, String point, String content){
        String[] addrs = SysUtility.getChannelAddr(ctx);
        ClientChannelLog entity = new ClientChannelLog();
        entity.setLogPoint(point);
        entity.setLogTime(new Date());
        entity.setLogContent(content);
        entity.setLocalAddress(addrs[0]);
        entity.setLocalPort(addrs[1]);
        entity.setRemoteAddress(addrs[2]);
        entity.setRemotePort(addrs[3]);
        if(SysUtility.isNotEmpty(topeMsg)){
            entity.setUserId(topeMsg.getFixedHeader().getUserId());
            entity.setMessageSource(topeMsg.getFixedHeader().getMessageSource());
            entity.setMessageDest(topeMsg.getFixedHeader().getMessageDest());
        }
        pushClientChannelLogQueue(entity);
//        WheelTimerUtils.addClientChannelLogTask(topeKernelProperties, false);
    }


    public static void pushServerChannelActiveQueue(ChannelHandlerContext ctx, WhyMessageFixedHeader hearders, String channelKey){
        ServerChannelActive entity = new ServerChannelActive();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setConnectTime(new Date());
        entity.setRemoteIp(SysUtility.getCtxRemoteIp(ctx));
        entity.setMessageWay(hearders.getMessageWay());
        entity.setMessageType(hearders.getMessageType());
        entity.setMessageSource(hearders.getMessageSource());
        entity.setMessageDest(hearders.getMessageDest());
        entity.setMessageSerialize(hearders.getMessageSerialize());
        pushServerChannelActiveQueue(entity);
    }

    public static void pushServerChannelInactiveQueue(ChannelHandlerContext ctx, String channelKey){
        ServerChannelInactive entity = new ServerChannelInactive();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setDisConnectTime(new Date());
        pushServerChannelInactiveQueue(entity);
    }

    public static void pushServerChannelExceptionCaughtQueue(ChannelHandlerContext ctx, String channelKey){
        ServerChannelExceptionCaught entity = new ServerChannelExceptionCaught();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setDisConnectTime(new Date());
        pushServerChannelExceptionCaughtQueue(entity);
    }

    public static void pushServerChannelIdleInactiveQueue(ChannelHandlerContext ctx, String channelKey){
//        ServerChannelInactive entity = new ServerChannelInactive();
//        entity.setChannelId(ctx.channel().id().asLongText());
//        entity.setChannelKey(channelKey);
//        entity.setDisConnectTime(new Date());
//        pushServerChannelIdleInactiveQueue(entity);
    }

    public static void pushServerChannelIdleActiveQueue(ChannelHandlerContext ctx, WhyMessageFixedHeader hearders, String channelKey){
        String[] addrs = SysUtility.getChannelAddr(ctx);

        ServerChannelIdleActive entity = new ServerChannelIdleActive();
        entity.setChannelId(ctx.channel().id().asLongText());
        entity.setChannelKey(channelKey);
        entity.setIdleTime(new Date());
        entity.setLocalAddress(addrs[0]);
        entity.setLocalPort(addrs[1]);
        entity.setRemoteAddress(addrs[2]);
        entity.setRemotePort(addrs[3]);
        entity.setMessageWay(hearders.getMessageWay());
        entity.setMessageType(hearders.getMessageType());
        entity.setMessageSource(hearders.getMessageSource());
        entity.setMessageDest(hearders.getMessageDest());
        entity.setMessageSerialize(hearders.getMessageSerialize());
        pushServerChannelIdleActiveQueue(entity);
//        WheelTimerUtils.addServerChannelIdleActiveTask(topeKernelProperties, false);
    }

}
