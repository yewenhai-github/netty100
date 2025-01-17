package com.netty100.broker.devops.queue;

import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.entity.ClientMessageLog;
import com.netty100.broker.devops.entity.KernelMessageLog;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author why
 * @version 1.0.0, 2022/5/30
 * @since 1.0.0, 2022/5/30
 */
public class WhyMessageQueue {
    private static WhyKernelProperties whyKernelProperties;

    /****************************消息日志****************************/
    public LinkedBlockingQueue<ClientMessageLog> clientMessageLogQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<KernelMessageLog> kernelMessageLogQueue = new LinkedBlockingQueue<>();

    private static WhyMessageQueue instance = new WhyMessageQueue();
    public static WhyMessageQueue getInstance(){
        if(SysUtility.isEmpty(whyKernelProperties)){
            whyKernelProperties = WhySpringUtils.getBean(WhyKernelProperties.class);
        }
        return instance;
    }

    public static void pushClientMessageLogQueue(ClientMessageLog entity){
        getInstance().clientMessageLogQueue.offer(entity);
    }

    public static void pushKernelMessageLogQueue(KernelMessageLog entity){
        getInstance().kernelMessageLogQueue.offer(entity);
    }

    public static void pushClientMessageLogQueue(ChannelHandlerContext ctx, String point, WhyMessage whyMsg, String content){
        pushClientMessageLogQueue(ctx, point, whyMsg, content, 1);
    }

    public static void pushClientMessageLogQueue(ChannelHandlerContext ctx, String point, WhyMessage whyMsg, String content, Integer leaf){
        String[] addrs = SysUtility.getChannelAddr(ctx);
        ClientMessageLog entity = new ClientMessageLog();
        entity.setLogPoint(point);
        entity.setLogTime(SysUtility.getSysDateWithMis());
        entity.setLogContent(content);
        entity.setLocalAddress(addrs[0]);
        entity.setLocalPort(addrs[1]);
        entity.setRemoteAddress(addrs[2]);
        entity.setRemotePort(addrs[3]);
        entity.setUserId(whyMsg.getFixedHeader().getUserId());
        entity.setDeviceId(whyMsg.getFixedHeader().getDeviceId());
        entity.setMessageId(whyMsg.getFixedHeader().getMessageId());
        entity.setMessageType(whyMsg.getFixedHeader().getMessageType());
        entity.setMessageWay(whyMsg.getFixedHeader().getMessageWay());
        entity.setMessageSerialize(whyMsg.getFixedHeader().getMessageSerialize());
        entity.setMessageSource(whyMsg.getFixedHeader().getMessageSource());
        entity.setMessageDest(whyMsg.getFixedHeader().getMessageDest());
        entity.setLeaf(leaf);
        pushClientMessageLogQueue(entity);
//        WheelTimerUtils.addClientMessageLogTask(WhyKernelProperties, false);
    }

    public static void pushKernelMessageLogQueue(WhyMessage whyMsg, String point, String content){
        KernelMessageLog entity = new KernelMessageLog();
        entity.setLogPoint(point);
        entity.setLogTime(new Date());
        entity.setLogContent(content);
        entity.setLocalAddress(SysUtility.getHostIp());
        if(SysUtility.isNotEmpty(whyMsg)){
            entity.setUserId(whyMsg.getFixedHeader().getUserId());
            entity.setDeviceId(whyMsg.getFixedHeader().getDeviceId());
            entity.setMessageSource(whyMsg.getFixedHeader().getMessageSource());
            entity.setMessageDest(whyMsg.getFixedHeader().getMessageDest());
        }
        pushKernelMessageLogQueue(entity);
//        WheelTimerUtils.addKernelMessageLogTask(WhyKernelProperties, false);
    }
}
