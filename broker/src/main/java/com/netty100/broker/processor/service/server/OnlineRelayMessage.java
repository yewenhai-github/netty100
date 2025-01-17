package com.netty100.broker.processor.service.server;

import com.netty100.cluster.naming.core.InstanceOperatorClientImpl;
import com.netty100.broker.annotation.EnableMessageType;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.broker.utils.WhyChannelUtils;
import com.netty100.broker.utils.WhyCountUtils;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageCode;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@EnableMessageType(value = {CommonConstants.type_online_relay_point_message, CommonConstants.type_online_relay_all_message})
public class OnlineRelayMessage implements MessageTypeService {
    @Autowired
    private WhyCloudUtils whyCloudUtils;
    @Autowired
    private InstanceOperatorClientImpl instanceService;
    @Autowired
    private WhyKernelProperties whyKernelProperties;

    @Override
    public boolean doCommand(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyNettyRemoting remotingClient, WhyKernelProperties kernelConfig) {
        try {
            String channelKey = WhyChannelUtils.getCurrentC2pChannelKey(whyMsg);
            Channel channel = WhyChannelUtils.getChannel(channelKey);

            if (SysUtility.isEmpty(channel) && WhyMessageCode.type_online_relay_point_message.getCode() == whyMsg.getFixedHeader().getMessageType()) {
                //定点发送消费失败，表示从redis取到的ip地址为错误地址，需要清除
//                WhyChannelUtils.updateIntranetIp(channelKey, "");
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M43.getCode(), whyMsg, LogPointCode.M43.getMessage()+"用户不在线");
                return false;//丢弃这一次消息
            }else if (SysUtility.isEmpty(channel) && WhyMessageCode.type_online_relay_all_message.getCode() == whyMsg.getFixedHeader().getMessageType()) {
                //群发消费触发消费成功，表示redis并未记录转发地址，需要记录
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M46.getCode(), whyMsg, LogPointCode.M46.getMessage()+"用户不在线");
                return false;//丢弃这一次消息
            }else if (SysUtility.isNotEmpty(channel) && WhyMessageCode.type_online_relay_all_message.getCode() == whyMsg.getFixedHeader().getMessageType()) {
//                WhyChannelUtils.updateIntranetIp(channelKey, "");
//              TODO  WhyChannelUtils.updateInstanceIntranetIp(channelKey, SysUtility.getHostIp(), ctx);
            }

            channel.writeAndFlush(WhyMessageFactory.getClientWhyMessage(whyMsg).bytes());

            if (WhyMessageCode.type_online_relay_point_message.getCode() == whyMsg.getFixedHeader().getMessageType()) {
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M44.getCode(), whyMsg, LogPointCode.M44.getMessage());
            }else if (WhyMessageCode.type_online_relay_all_message.getCode() == whyMsg.getFixedHeader().getMessageType()) {
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M47.getCode(), whyMsg, LogPointCode.M47.getMessage());
            }
        } catch (Exception e) {
            log.error("用户{},消息发送失败{}", whyMsg.getFixedHeader().getUserId(), e);
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M48.getCode(), whyMsg, LogPointCode.M48.getMessage() + SysUtility.getErrorMsg(e));
            return false;
        }
        return true;
    }


    public boolean doRelayProducer(ChannelHandlerContext ctx, WhyMessage whyMsg, WhyNettyRemoting remotingClient) {
        //消息转发
        String key = WhyChannelUtils.getCurrentC2pChannelKey(whyMsg);
        String addr = WhyChannelUtils.getIntranetIp(key);
        if(SysUtility.isEmpty(addr)){
            //群发消息
            AtomicBoolean sendFlag = new AtomicBoolean(false);
            whyMsg.getFixedHeader().setMessageType(WhyMessageCode.type_online_relay_all_message.getCode());
            whyCloudUtils.getAllServiceUrls().stream().forEach(address ->{
                if(!address.startsWith(SysUtility.getHostIp())){
                    sendRelayMessage(whyMsg, remotingClient, addrCompletion(address));
                    sendFlag.set(true);
                }
            });
            if(sendFlag.get()){
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M45.getCode(), whyMsg, LogPointCode.M45.getMessage() + whyCloudUtils.getAllServiceUrls().toString());
            }
            return true;
        }else if(!addr.startsWith(SysUtility.getHostIp())){
            //定点机器发送
            whyMsg.getFixedHeader().setMessageType(WhyMessageCode.type_online_relay_point_message.getCode());
            boolean rt = sendRelayMessage(whyMsg, remotingClient, addrCompletion(addr));
            if(rt){
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M41.getCode(), whyMsg, LogPointCode.M41.getMessage() + "转发地址=" + addr);
            }else{
                WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M42.getCode(), whyMsg, LogPointCode.M42.getMessage() + "转发地址=" + addr);
            }
            return true;
        }
        return false;
    }

    String addrCompletion(String addr){
        return addr.indexOf(":") >0 ? addr : addr + ":" + whyKernelProperties.getPort();
    }


    private boolean sendRelayMessage(WhyMessage whyMsg, WhyNettyRemoting remotingClient, String addr) {
        byte [] msg = whyMsg.bytes();
        boolean rt = remotingClient.invokeOneway(addr, msg, 3000L);
        if(!rt){
            //定点发送消费失败，表示从redis取到的ip地址为错误地址，需要清除
//            String channelKey = WhyChannelUtils.getCurrentC2pChannelKey(whyMsg);

//            InstanceForm instanceForm = WhyChannelUtils.defaultInstanceForm(channelKey, WhyKernelProperties.getPort());
//            Instance instance = WhyChannelUtils.buildInstance(instanceForm);
//            instance.setIntranetIp(SysUtility.getHostIp());
//            instanceService.removeInstance(instanceForm.getNamespaceId(), WhyChannelUtils.buildCompositeServiceName(instanceForm), instance, channelKey);

//            WhyChannelUtils.updateIntranetIp(channelKey, "");;
            log.info("消息转发至{}失败，messageId={}，丢失此消息，redis地址更新成功..", addr, whyMsg.getFixedHeader().getMessageId());
        }else{
            WhyCountUtils.platform_p2p_message_relay_total.add(1);
            WhyCountUtils.platform_p2p_message_relay_flow.add(msg.length);
        }
        return rt;
    }



}