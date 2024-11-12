package com.netty100.kernel.processor.service.server;

import com.netty100.kernel.annotation.EnableMessageType;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.devops.queue.WhyMessageQueue;
import com.netty100.kernel.devops.utils.WhyCloudUtils;
import com.netty100.kernel.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.kernel.utils.WhyChannelUtils;
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
 * @author yewenhai
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
    public boolean doCommand(ChannelHandlerContext ctx, WhyMessage topeMsg, WhyNettyRemoting remotingClient, WhyKernelProperties kernelConfig) {
        //如果是群发消息，先设置下需要再次群发的标识，进行集群内机器群发
        if(topeMsg.getFixedHeader().getMessageType() == CommonConstants.type_online_all_message){
            //设置消息类型为内部广播类型
            topeMsg.getFixedHeader().setMessageType(WhyMessageCode.type_online_broadcast_message.getCode());
            //通知其他服务器消费广播数据
            whyCloudUtils.getAllServiceUrls().stream().forEach((addr)->{
                if(!addr.startsWith(SysUtility.getHostIp())){
                    remotingClient.invokeOneway(addr, topeMsg.bytes(), 3000L);
                }
            });
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M61.getCode(), topeMsg, LogPointCode.M61.getMessage() + "广播地址=" + whyCloudUtils.getAllServiceUrls().toString());
        }

        //将群发的数据放入队列，异步消费
        messageBroadcastQueue.offer(topeMsg);
        WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M62.getCode(), topeMsg, LogPointCode.M62.getMessage() + "消费地址=" + SysUtility.getHostIp());
        return true;
    }

    //消费广播数据
    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void doConsumer() throws InterruptedException {
        WhyMessage topeMsg = messageBroadcastQueue.poll(3L, TimeUnit.SECONDS);
        if (SysUtility.isNotEmpty(topeMsg)) {
            WhyChannelUtils.getC2pCacheChannelMap().forEach((userId, channel) ->{
                MessageTypeService.super.doCommand(null, topeMsg, null, null);
            });
        }
    }

}