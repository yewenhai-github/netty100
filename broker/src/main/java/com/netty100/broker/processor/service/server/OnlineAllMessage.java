package com.netty100.broker.processor.service.server;

import com.netty100.broker.annotation.EnableMessageType;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageCode;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author why
 * @version 1.0.0, 2022/4/1
 * @since 1.0.0, 2022/4/1
 */
@Slf4j
@Data
@Component
@EnableMessageType(value = {CommonConstants.type_online_all_message, CommonConstants.type_online_broadcast_message})
public class OnlineAllMessage implements MessageTypeService {
    @Autowired
    private WhyCloudUtils whyCloudUtils;

    LinkedBlockingQueue<WhyMessage> messageBroadcastQueue = new LinkedBlockingQueue<WhyMessage>();

    @Override
    public boolean doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyNettyRemoting remotingClient, WhyKernelProperties kernelConfig) {
        //如果是群发消息，先设置下需要再次群发的标识，进行集群内机器群发
        if(whyMsg.getFixedHeader().getMessageType() == CommonConstants.type_online_all_message){
            //设置消息类型为内部广播类型
            whyMsg.getFixedHeader().setMessageType(WhyMessageCode.type_online_broadcast_message.getCode());
            //通知其他服务器消费广播数据
            whyCloudUtils.getAllServiceUrls().stream().forEach((addr)->{
                if(!addr.startsWith(SysUtility.getHostIp())){
                    remotingClient.invokeOneway(addr, whyMsg.bytes(), 3000L);
                }
            });
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M61.getCode(), whyMsg, LogPointCode.M61.getMessage() + "广播地址=" + whyCloudUtils.getAllServiceUrls().toString());
        }

        //将群发的数据放入队列，异步消费
        messageBroadcastQueue.offer(whyMsg);
        WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M62.getCode(), whyMsg, LogPointCode.M62.getMessage() + "消费地址=" + SysUtility.getHostIp());
        return true;
    }

    //消费广播数据
    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void doConsumer() throws InterruptedException {
        WhyMessage whyMsg = messageBroadcastQueue.poll(3L, TimeUnit.SECONDS);
        if (SysUtility.isNotEmpty(whyMsg)) {
            WhyChannelUtils.getC2pCacheChannelMap().forEach((userId, channel) ->{
                MessageTypeService.super.doCommand(null, whyMsg, null, null);
            });
        }
    }

}