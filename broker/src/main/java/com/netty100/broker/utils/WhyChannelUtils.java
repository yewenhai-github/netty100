package com.netty100.broker.utils;

import com.netty100.cluster.api.naming.pojo.Instance;
import com.netty100.cluster.api.naming.pojo.builder.InstanceBuilder;
import com.netty100.cluster.api.naming.utils.NamingUtils;
import com.netty100.cluster.naming.core.InstanceOperatorClientImpl;
import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import com.netty100.cluster.naming.misc.UtilsAndCommons;
import com.netty100.cluster.naming.model.form.InstanceForm;
import com.netty100.cluster.sys.utils.ClientCacheUtils;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.queue.WhyConnectQueue;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.common.chooser.ChooserSelector;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.protocol.ResponseCode;
import com.netty100.common.protocol.WhyMessage;
import com.netty100.common.protocol.WhyMessageFactory;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
public class WhyChannelUtils {
//    private static Map<String, Channel> ClientCacheUtils.getClients() = new ConcurrentHashMap();
    public static final AttributeKey<Map<String, Object>> CHANNEL_INFO_ATTRIBUTE_KEY = AttributeKey.valueOf("I");
    private static final String CHANNEL_ID_KEY = "0";
    private static final String CHANNEL_CLIENT_ID_KEY = "1";
    private static Map<String, List<Channel>> s2pCacheChannelMap = new ConcurrentHashMap();
    private static Map<ChannelId, String> s2pChannelTypeMap = new ConcurrentHashMap<>();
    public static final String s2pChannelType = "s2p";
    public static final String simplexChannelType = "simplex";
    public static final String s2pJobChannelType = "s2pJob";
    public static final String s2pMqChannelType = "s2pMq";


    public static final String SERVICE_INFO_SPLITER = "@@";
    public static final String DEFAULT_NAMESPACE_ID = "public";
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    public static final String DEFAULT_SERVICE_NAME = "DEFAULT_SERVICE_NAME";

    public static final boolean instanceEnabled = true;
    public static final String clusterName = "DEFAULT";
    public static final Double weight = 1d;


    public static Instance buildInstance(InstanceForm instanceForm) {
        Instance instance = null;
        try {
            instance = InstanceBuilder.newBuilder()
                    .setServiceName(buildCompositeServiceName(instanceForm))
                    .setIp(instanceForm.getIp())
                    .setClusterName(instanceForm.getClusterName())
                    .setPort(instanceForm.getPort())
                    .setHealthy(instanceForm.getHealthy())
                    .setWeight(instanceForm.getWeight())
                    .setEnabled(instanceForm.getEnabled())
                    .setMetadata(UtilsAndCommons.parseMetadata(instanceForm.getMetadata()))
                    .setEphemeral(instanceForm.getEphemeral())
                    .build();
            if (instanceForm.getEphemeral() == null) {
                instance.setEphemeral(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }
    public static String buildCompositeServiceName(InstanceForm instanceForm) {
        return NamingUtils.getGroupedName(instanceForm.getServiceName(), instanceForm.getGroupName());
    }

    public static String getCurrentC2pChannelKey(WhyMessage topeMsg){
        return topeMsg.getFixedHeader().getUserId()
                + IpPortBasedClient.ID_DELIMITER + topeMsg.getFixedHeader().getMessageSource()
                + IpPortBasedClient.ID_DELIMITER + topeMsg.getFixedHeader().getMessageDest();
    }

    public static String getCurrentS2pChannelKey(WhyMessage whyMessage){
        return whyMessage.getFixedHeader().getMessageSource() + IpPortBasedClient.ID_DELIMITER + whyMessage.getFixedHeader().getMessageDest();
    }

    public static String getCurrentS2pChannelKeySimplex(WhyMessage whyMessage){
        return whyMessage.getFixedHeader().getMessageSource() + IpPortBasedClient.ID_DELIMITER + whyMessage.getFixedHeader().getMessageDest()+ IpPortBasedClient.ID_DELIMITER + whyMessage.getFixedHeader().getMessageWay()+"_simplex";
    }

    public static String getCurrentS2pChannelKeyJob(WhyMessage whyMessage){
        return whyMessage.getFixedHeader().getMessageSource() + IpPortBasedClient.ID_DELIMITER + whyMessage.getFixedHeader().getMessageDest()+ IpPortBasedClient.ID_DELIMITER + whyMessage.getFixedHeader().getMessageWay()+"_job";
    }

    public static String getCurrentS2pChannelKeyMq(WhyMessage whyMessage) {
        return whyMessage.getFixedHeader().getMessageSource() + IpPortBasedClient.ID_DELIMITER + whyMessage.getFixedHeader().getMessageDest() + IpPortBasedClient.ID_DELIMITER + whyMessage.getFixedHeader().getMessageWay()+"_mq";
    }

    /**
     * 用户连接接入缓存
     * @param channelKey
     * @param ctx
     */
    public static void c2pChannelCache(String channelKey, ChannelHandlerContext ctx, WhyMessage topeMsg, WhyKernelProperties kernelConfig) {
        Channel channel = ctx.channel();
        Map<String, Object> newInfoAttribute = new ConcurrentHashMap<>(8);
        newInfoAttribute.put(CHANNEL_ID_KEY, getId(channel));
        newInfoAttribute.put(CHANNEL_CLIENT_ID_KEY, channelKey);
        channel.attr(CHANNEL_INFO_ATTRIBUTE_KEY).set(newInfoAttribute);

        //缓存channelId，用于关闭管道时同步清空客户端连接缓存ClientCacheUtils.getClients()
//        c2pChannelIdMap.put(ctx.channel().id(), channelKey);
        //Redis缓存所有管道的关系，用于内部消息转发定位到具体服务节点
//        SpringUtils.getBean(RedisBoundUtils.class).putC2pCacheChannel(channelKey, TopeCloudUtils.getLocalServiceUrls());
//        TopeReplicateUtils.setC2pIpCache(channelKey, SysUtility.getHostIp()+":"+kernelConfig.getPort(), true);
    }

    public static boolean c2pChannelSingleSame(String channelKey, ChannelHandlerContext ctx){
        if(ClientCacheUtils.getClients().containsKey(channelKey)){
            Channel c = ClientCacheUtils.getChannel(channelKey);
            if(SysUtility.isNotEmpty(c) && c.id() != ctx.channel().id()){
                return true;
            }
        }
        return false;
    }

    public static void c2pChannelForceClose(WhyMessage topeMsg, String channelKey, ChannelHandlerContext ctx, byte[] body, String point, String content) {
        if(SysUtility.isEmpty(channelKey)){
            return;
        }
        if(ClientCacheUtils.getClients().containsKey(channelKey) && SysUtility.isNotEmpty(ClientCacheUtils.getClients().get(channelKey))){
            Channel channel = ClientCacheUtils.getChannel(channelKey);
            try {
                if(SysUtility.isEmpty(channel)){
                    return;
                }

                //移除内存
//                c2pChannelIdMap.remove(channel.id());
                ClientCacheUtils.getClients().remove(channelKey);
                //统计量收集
                WhyCountUtils.platform_c2p_connect_inactive_total.add(1);
                //响应客户端
                WhyMessage topeMsgForceClose = WhyMessageFactory.newMessage(topeMsg, body);
                channel.writeAndFlush(topeMsgForceClose.bytes());
                //日志埋点
                WhyConnectQueue.pushClientChannelLogQueue(ctx, topeMsg, point, content);
                //加入清空队列
                WhyChannelPreClose.proClose(channel);
            } catch (Exception e) {
                log.error("用户{}下线失败:{}", channelKey, e);
            }
        }
    }

    /**
     * 关闭管道，并移除缓存数据
     * @param ctx
     *
     * **/
    public static boolean c2pChannelCacheDelete(ChannelHandlerContext ctx, String channelKey){
        if(SysUtility.isEmpty(channelKey) || !ClientCacheUtils.getClients().containsKey(channelKey)){
            return false;
        }
        try {
//            c2pChannelIdMap.remove(ctx.channel().id());

            ConcurrentMap<String, IpPortBasedClient> clients = ClientCacheUtils.getClients();
            IpPortBasedClient ipPortBasedClient = clients.get(channelKey);
            if(SysUtility.isEmpty(ipPortBasedClient)){
                return false;
            }
            InstanceForm instanceForm = WhyChannelUtils.defaultInstanceForm(channelKey, getInstanceTkp().getPort());
            Instance instance = WhyChannelUtils.buildInstance(instanceForm);
            instance.setIntranetIp(SysUtility.getHostIp());
            getInstanceIoci().removeInstance(instanceForm.getNamespaceId(), WhyChannelUtils.buildCompositeServiceName(instanceForm), instance, channelKey);

            ClientCacheUtils.getClients().remove(channelKey);
        } catch (Exception e) {
            log.error("用户{}下线失败:{}", channelKey, e);
        }
        return true;
    }

    public static boolean s2pChannelCacheDelete(ChannelHandlerContext ctx){
        try {
            //遍历所有集群，找到对应的channel，移除并关闭
            boolean successful = delS2pCacheChannel(ctx);
            if(successful){
                return true;
            }else{
                String[] addrs = SysUtility.getChannelAddr(ctx);
                log.error("服务器管道删除失败：LocalAddress={}，LocalPort={}，RemoteAddress={}，RemotePort={}", addrs[0], addrs[1], addrs[2], addrs[3]);
                return false;
            }
        } catch (Exception e) {
            log.error("服务器下线失败:{}" , e);
        }
        return false;
    }

//    public static boolean c2pCloseCacheCtx(String channelKey, TopeMessage topeMsg){
//        if(SysUtility.isEmpty(channelKey) || !ClientCacheUtils.getClients().containsKey(channelKey)){
//            return false;
//        }
//        Channel channel = ClientCacheUtils.getChannel(channelKey);
//        try {
//            if(SysUtility.isEmpty(channel)){
//                return false;
//            }
//            c2pChannelIdMap.remove(channel.id());
//            removeInstanceIntranetIp(channelKey);
//            ClientCacheUtils.getClients().remove(channelKey);
//            TopeMessage topeMsgForceClose = TopeMessageFactory.newMessage(topeMsg, ResponseCode.Rep118.getCodeBytes());
//            channel.writeAndFlush(topeMsgForceClose.bytes());
//        } catch (Exception e) {
//            log.error("用户{}下线失败:{}", channelKey, e);
//        } finally {
//            log.info("用户强制下线成功，channelKey={},channelId={}", channelKey, channel.id());
//            channel.close();
//        }
//        return true;
//    }

    /**
     * 获取用户id
     *
     * @param ctx
     * @return
     */
    public static String getChannelKeyByCtx(ChannelHandlerContext ctx) {
        return (String) getInfo(ctx.channel()).get(CHANNEL_CLIENT_ID_KEY);
    }

    public static String getS2pChannelType(ChannelHandlerContext ctx){
        return s2pChannelTypeMap.get(ctx.channel().id());
    }

    public static Long getClientUserCount() {
        return ClientCacheUtils.getClients().size() + 0L;
    }

    public static Long getServerUserCount() {
        Long rt = new Long(0L);
        for(String clusterKey: s2pCacheChannelMap.keySet()){
            List<Channel> s2pCacheChannelList = getS2pCacheChannelList(clusterKey);
            rt += s2pCacheChannelList.size();
        }
        return rt;
    }

    public static Long getChannelCount() {
        return getClientUserCount() + getServerUserCount();
    }


    public static Map<String, IpPortBasedClient> getC2pCacheChannelMap() {
        return ClientCacheUtils.getClients();
    }

    public static Channel getChannel(String channelKey){
        IpPortBasedClient ipPortBasedClient = WhyChannelUtils.getC2pCacheChannelMap().get(channelKey);
        if(SysUtility.isEmpty(ipPortBasedClient)){
            return null;
        }
        return ipPortBasedClient.getChannel();
    }

    public static void writeAndFlush(ChannelHandlerContext ctx, WhyMessage msg){
        ctx.writeAndFlush(msg.bytes());
    }

    public static void p2sWriteAndFlush(ChannelHandlerContext ctx, WhyMessage topeMsg, int serverCacheChannelReTimes) {
        String clusterKey = getCurrentS2pChannelKey(topeMsg);
        Channel c = getS2pCacheChannel(clusterKey, topeMsg, serverCacheChannelReTimes);
        p2sWriteAndFlush(c, ctx, topeMsg, serverCacheChannelReTimes);
    }

    public static void p2sSimplexWriteAndFlush(ChannelHandlerContext ctx, WhyMessage topeMsg, int serverCacheChannelReTimes) {
        Channel c = getS2pCacheChannel(getCurrentS2pChannelKeySimplex(topeMsg), topeMsg, serverCacheChannelReTimes);
        //如果没有找到对应的channel，尝试从双工集群中获取
        if(SysUtility.isEmpty(c)){
            c = getS2pCacheChannel(getCurrentS2pChannelKey(topeMsg), topeMsg, serverCacheChannelReTimes);
        }
        p2sWriteAndFlush(c, ctx, topeMsg, serverCacheChannelReTimes);
    }
    public static void p2sWriteAndFlush(Channel c, ChannelHandlerContext ctx, WhyMessage topeMsg, int serverCacheChannelReTimes) {
        if(SysUtility.isNotEmpty(c)){
            c.writeAndFlush(topeMsg.bytes());
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M02.getCode(), topeMsg, LogPointCode.M02.getMessage() + "<" + c.remoteAddress() + ">");
        }else {
            topeMsg.getFixedHeader().setMessageWay(CommonConstants.way_c2p_channelActive);
            topeMsg = WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep115.getCodeBytes());
            ctx.writeAndFlush(topeMsg.bytes());
            WhyMessageQueue.pushClientMessageLogQueue(ctx, LogPointCode.M03.getCode(), topeMsg, LogPointCode.M03.getMessage()+"<"+ WhyChannelUtils.getCurrentS2pChannelKey(topeMsg)+">");
        }
    }


    public static boolean addS2pCacheChannel(String clusterKey, String channelType, ChannelHandlerContext ctx){
        synchronized (s2pCacheChannelMap){
            Channel channel = ctx.channel();
            s2pChannelTypeMap.put(channel.id(), channelType);
            List<Channel> s2pCacheChannelList = getS2pCacheChannelList(clusterKey);
            boolean add = s2pCacheChannelList.add(channel);
            String[] addrs = SysUtility.getChannelAddr(ctx);
            if(add){
//                log.warn("服务器管道（add success）：List.size()={},clusterKey={}，channelType={}，LocalAddress={}，LocalPort={}，RemoteAddress={}，RemotePort={}", s2pCacheChannelList.size(), clusterKey, channelType, addrs[0], addrs[1], addrs[2], addrs[3]);
            }else {
                log.error("服务器管道（add fail）：List.size()={},clusterKey={}，channelType={}，LocalAddress={}，LocalPort={}，RemoteAddress={}，RemotePort={}", s2pCacheChannelList.size(), clusterKey, channelType, addrs[0], addrs[1], addrs[2], addrs[3]);
            }

            return add;
        }
    }

    public synchronized static boolean delS2pCacheChannel(ChannelHandlerContext ctx){
        synchronized (s2pCacheChannelMap){
            Channel channel = ctx.channel();

            boolean deleteResultFlag = false;
            String clusterKey = "";
            String channelType = "";
            int size = 0;
            for(String tempClusterKey: s2pCacheChannelMap.keySet()){
                List<Channel> s2pCacheChannelList = getS2pCacheChannelList(tempClusterKey);
                if(s2pCacheChannelList.contains(channel)){
                    channelType = s2pChannelTypeMap.remove(channel.id());
                    deleteResultFlag = s2pCacheChannelList.remove(channel);
                    clusterKey = tempClusterKey;
                    size = s2pCacheChannelList.size();
                    break;
                }
            }

            String[] addrs = SysUtility.getChannelAddr(ctx);
            if(deleteResultFlag){
//                log.warn("服务器管道（delete success）：List.size()={},clusterKey={}，channelType={}，LocalAddress={}，LocalPort={}，RemoteAddress={}，RemotePort={}", size, clusterKey, channelType, addrs[0], addrs[1], addrs[2], addrs[3]);
            }else {
                log.error("服务器管道（delete fail）：List.size()={},clusterKey={}，channelType={}，LocalAddress={}，LocalPort={}，RemoteAddress={}，RemotePort={}", size, clusterKey, channelType, addrs[0], addrs[1], addrs[2], addrs[3]);
            }

            return deleteResultFlag;
        }
    }

    public static List<Channel> getS2pCacheChannelList(String clusterKey){
        List<Channel> s2pCacheChannelList = s2pCacheChannelMap.get(clusterKey);
        if(SysUtility.isEmpty(s2pCacheChannelList)){
            s2pCacheChannelList = new ArrayList<>();
            s2pCacheChannelMap.put(clusterKey, s2pCacheChannelList);
        }
        return s2pCacheChannelList;
    }

    /**
     * 获取server端缓存的Channel
     * */
    public static Channel getS2pCacheChannel(String clusterKey, WhyMessage topeMsg, int serverCacheChannelReTimes) {
        List<Channel> s2pCacheChannelList = getS2pCacheChannelList(clusterKey);
        if(serverCacheChannelReTimes <= 0){
            log.error("服务器管道获取失败，已重试{}次，当前管道数={}",serverCacheChannelReTimes, s2pCacheChannelList.size());
            return null;
        }
        if(SysUtility.isEmpty(s2pCacheChannelList)){
            log.error("服务器管道获取失败，当前管道数={}，clusterKey={}", s2pCacheChannelList.size(), clusterKey);
            return null;
        }

        Channel c = null;
        try {
            c = (Channel) ChooserSelector.getLoopChooser().next(s2pCacheChannelList);
        } catch (Exception e) {
            log.error("服务器管道获取失败，已重试{}次，当前管道数={}，异常={}" ,serverCacheChannelReTimes, s2pCacheChannelList.size(), e);
        } finally {
            if(SysUtility.isEmpty(c)){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("异常错误{}", e);
                }
                serverCacheChannelReTimes --;
                return getS2pCacheChannel(clusterKey, topeMsg, serverCacheChannelReTimes);
            }
        }
        return c;
    }





    public static String getOfflineSingleKeys(WhyMessage topeMsg){
        String key = WhyCloudUtils.clusterId + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageSource() + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageDest()
                + WhyCloudUtils.separator + topeMsg.getFixedHeader().getUserId()
                + WhyCloudUtils.separator + topeMsg.getFixedHeader().getUserId()+"*";
        return key;
    }

    //广播消息遍历的key
    public static String getOfflineAllKeys(WhyMessage topeMsg){
        String key = WhyCloudUtils.clusterId + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageSource() + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageDest()
                + WhyCloudUtils.separator + "all"+"*";
        return key;
    }

    //广播消息消费后，用户存储已消费的关联记录
    public static String getOfflineAllAlreadyConsumerKey(WhyMessage topeMsg){
        String key = WhyCloudUtils.clusterId + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageSource() + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageDest()
                + WhyCloudUtils.separator + "old"
                + WhyCloudUtils.separator + topeMsg.getFixedHeader().getUserId()
                + WhyCloudUtils.separator + topeMsg.getFixedHeader().getMessageId();
        return key;
    }

    //新建
    public static String getOfflineSingleKey(WhyMessage topeMsg){
        String key = WhyCloudUtils.clusterId + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageSource() + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageDest()
                + WhyCloudUtils.separator + topeMsg.getFixedHeader().getUserId()
                + WhyCloudUtils.separator + topeMsg.getFixedHeader().getMessageId();
        return key;
    }

    //新建
    public static String getOfflineAllKey(WhyMessage topeMsg){
        String key = WhyCloudUtils.clusterId + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageSource() + WhyCloudUtils.separator_ + topeMsg.getFixedHeader().getMessageDest()
                + WhyCloudUtils.separator + "all"
                + WhyCloudUtils.separator + topeMsg.getFixedHeader().getMessageId();
        return key;
    }


    public static InstanceForm defaultInstanceForm(String clientId, Integer port) {
        InstanceForm instanceForm = new InstanceForm();
        instanceForm.setNamespaceId(WhyChannelUtils.DEFAULT_NAMESPACE_ID);
        instanceForm.setGroupName(WhyChannelUtils.DEFAULT_GROUP);
        instanceForm.setServiceName(WhyChannelUtils.DEFAULT_SERVICE_NAME);
        instanceForm.setIp(SysUtility.getHostIp());
        instanceForm.setPort(port);
        instanceForm.setWeight(WhyChannelUtils.weight);
        instanceForm.setClusterName(WhyChannelUtils.clusterName);
        instanceForm.setEnabled(WhyChannelUtils.instanceEnabled);
        instanceForm.setMetadata("");
        instanceForm.setEphemeral(true);
        instanceForm.setClientId(clientId);
        return instanceForm;
    }

    private static WhyKernelProperties whyKernelProperties;
    private static InstanceOperatorClientImpl instanceService;

    private static WhyKernelProperties getInstanceTkp(){
        if(SysUtility.isEmpty(whyKernelProperties)){
            whyKernelProperties = WhySpringUtils.getBean(WhyKernelProperties.class);
        }
        return whyKernelProperties;
    }

    private static InstanceOperatorClientImpl getInstanceIoci(){
        if(SysUtility.isEmpty(instanceService)){
            instanceService = WhySpringUtils.getBean(InstanceOperatorClientImpl.class);
        }
        return instanceService;
    }

    public static String getIntranetIp(String channelKey){
        IpPortBasedClient ipPortBasedClient = ClientCacheUtils.getClients().get(channelKey);
        if(SysUtility.isEmpty(ipPortBasedClient)){
            return "";
        }
        return ipPortBasedClient.getIntranetIp();
    }

//    public static void updateIntranetIp(String channelKey, String intranetIp) {
//        ConcurrentMap<String, IpPortBasedClient> clients = ClientCacheUtils.getClients();
//        IpPortBasedClient ipPortBasedClient = clients.get(channelKey);
//        ipPortBasedClient.setIntranetIp(intranetIp);

//        InstanceForm instanceForm = TopeChannelUtils.defaultInstanceForm(channelKey, getInstanceTkp().getPort());
//        Instance instance = TopeChannelUtils.buildInstance(instanceForm);
//        instance.setIntranetIp(intranetIp);
//        getInstanceIoci().updateInstance(instanceForm.getNamespaceId(), TopeChannelUtils.buildCompositeServiceName(instanceForm), instance);
//    }




    public static String getId(Channel channel) {
        Map<String, Object> info = getInfo(channel);
        if (info.containsKey(CHANNEL_ID_KEY)) {
            return (String)info.get(CHANNEL_ID_KEY);
        }
        String channelIdStr = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        info.put(CHANNEL_ID_KEY, channelIdStr);
        return channelIdStr;
    }

    public static Map<String, Object> getInfo(Channel channel) {
        Attribute<Map<String, Object>> infoAttribute = channel.attr(CHANNEL_INFO_ATTRIBUTE_KEY);
        if (infoAttribute.get() == null) {
            infoAttribute.setIfAbsent(new ConcurrentHashMap<>(8));
        }
        return infoAttribute.get();
    }
}
