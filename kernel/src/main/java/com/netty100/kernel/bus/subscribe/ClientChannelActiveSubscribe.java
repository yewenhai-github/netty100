package com.netty100.kernel.bus.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.netty100.cluster.api.naming.pojo.Instance;
import com.netty100.cluster.naming.constants.ClientConstants;
import com.netty100.cluster.naming.core.InstanceOperatorClientImpl;
import com.netty100.cluster.naming.core.v2.client.ClientAttributes;
import com.netty100.cluster.naming.model.form.InstanceForm;
import com.netty100.cluster.naming.web.ClientAttributesFilter;
import com.netty100.common.protocol.*;
import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.devops.queue.WhyConnectQueue;
import com.netty100.kernel.devops.utils.WhyCloudUtils;
import com.netty100.kernel.protocol.LogPointCode;
import com.netty100.kernel.protocol.TimerRunConstants;
import com.netty100.remoting.WhyNettyRemoting;
import com.netty100.kernel.utils.WhyChannelUtils;
import com.netty100.kernel.utils.WhyCountUtils;
import com.netty100.common.http.RemotingHttpResult;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClientChannelActiveSubscribe {

    @Autowired
    private WhyKernelProperties whyKernelProperties;
    @Autowired
    private WhyCloudUtils whyCloudUtils;
    @Autowired
    private InstanceOperatorClientImpl instanceService;

    @Subscribe
    @AllowConcurrentEvents
    public void onEvent(ClientChannelActiveData event) {
        // 处理消息
        WhyMessage topeMsg = event.getWhyMessage();
        ChannelHandlerContext ctx = event.getCtx();
        WhyKernelProperties kernelConfig = event.getKernelConfig();
        WhyNettyRemoting remotingClient = event.getRemotingClient();

        try {
            String clientId = WhyChannelUtils.getCurrentC2pChannelKey(topeMsg);
            //强制用户退出功能处理
            if(kickOutSameUserNext(clientId, ctx, topeMsg, remotingClient)){
                return;
            }

            //没有开启云端连接功能，不做用户注册码与配置校验
            if(!SysUtility.isEmpty(whyKernelProperties.getDomain())){
                //用户注册码与配置校验
                if(!doCommandConfigValidate(clientId, ctx, topeMsg) || !doCommandRegisterValidate(clientId, ctx, topeMsg)){
                    return;
                }
            }

            //客户端用户在线channel缓存
            InstanceForm instanceForm = WhyChannelUtils.defaultInstanceForm(clientId, whyKernelProperties.getPort());
            Instance instance = WhyChannelUtils.buildInstance(instanceForm);
            instance.setChannel(ctx.channel());
            instance.setIntranetIp(SysUtility.getHostIp());

            ClientAttributes attributes = new ClientAttributes();
            attributes.addClientAttribute(ClientConstants.CHANNEL, instance.getChannel());
            attributes.addClientAttribute(ClientConstants.INTRANETIP, instance.getIntranetIp());
            ClientAttributesFilter.threadLocalClientAttributes.set(attributes);

            instanceService.registerInstance(instanceForm.getNamespaceId(), WhyChannelUtils.buildCompositeServiceName(instanceForm), instance, clientId, ctx);
            //客户端channel关系缓存
            WhyChannelUtils.c2pChannelCache(clientId, ctx, topeMsg, kernelConfig);

            //消息响应
            WhyChannelUtils.writeAndFlush(ctx, WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep101.getCodeBytes()));
            //调用服务器的ctx，将userId传递到服务器的ctx
            WhyChannelUtils.p2sWriteAndFlush(ctx, topeMsg, kernelConfig.getServerCacheChannelReTimes());

            //加载离线消息
//            consumerOfflineSingle(topeMsg);
            //统计信息收集
            if(SysUtility.isNotEmpty(WhyChannelUtils.getChannel(clientId))){
                WhyCountUtils.platform_c2p_connect_active_total.add(1);
            }
            //信息收集
            WhyConnectQueue.pushClientChannelActiveQueue(ctx, topeMsg.getFixedHeader(), clientId);
            //连接日志收集
            WhyConnectQueue.pushClientChannelLogQueue(ctx, topeMsg, LogPointCode.C01.getCode(), LogPointCode.C01.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private boolean kickOutSameUserNext(String channelKey, ChannelHandlerContext ctx, WhyMessage topeMsg, WhyNettyRemoting remotingClient){
        //将客户端在集群内在线的用户踢下线（consumer）
        if(WhyMessageCode.type_offline_client_user.getCode() == topeMsg.getFixedHeader().getMessageType()){
            WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep116.getCodeBytes(), LogPointCode.C16.getCode(), LogPointCode.C16.getMessage());
            return true;
        }

        //将客户端在本机重复登录的上一个用户踢下线
        if(WhyChannelUtils.c2pChannelSingleSame(channelKey, ctx)){
            WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep118.getCodeBytes(), LogPointCode.C18.getCode(), LogPointCode.C18.getMessage());
        }
        //将客户端在集群内在线的用户踢下线（producer）
        whyCloudUtils.getAllServiceUrls().stream().forEach(address ->{
            WhyMessage whyMessage = WhyMessageFactory.newMessage(topeMsg, topeMsg.getPayload());
            whyMessage.getFixedHeader().setMessageType(WhyMessageCode.type_offline_client_user.getCode());
            byte [] msg = whyMessage.bytes();
            if(!address.startsWith(SysUtility.getHostIp())){
                remotingClient.invokeOneway(address, msg, 3000L);
            }
        });
        return false;
    }

    /**
     * 连接报文新增消息协议配置校验
     * */
    private boolean doCommandConfigValidate(String channelKey, ChannelHandlerContext ctx, WhyMessage topeMsg){
        if(SysUtility.isEmpty(whyKernelProperties)){
            whyKernelProperties = WhySpringUtils.getBean(WhyKernelProperties.class);
        }
        if(topeMsg.getFixedHeader().getMessageSource() < 0){
            WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep112.getCodeBytes()));
            WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep112.getCodeBytes(), LogPointCode.C08.getCode(), LogPointCode.C08.getMessage());
            return false;
        }
        if(topeMsg.getFixedHeader().getMessageDest() < 0){
            WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep113.getCodeBytes()));
            WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep113.getCodeBytes(), LogPointCode.C09.getCode(), LogPointCode.C09.getMessage());
            return false;
        }
        //校验
        String configStr = WhyChannelUtils.getCurrentS2pChannelKey(topeMsg);
        if(!WhyCloudUtils.serviceConfigs.contains(configStr)){
            WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep114.getCodeBytes()));
            WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep114.getCodeBytes(), LogPointCode.C10.getCode(), LogPointCode.C10.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 连接报文新增设备注册码校验
     * */
    private boolean doCommandRegisterValidate(String channelKey, ChannelHandlerContext ctx, WhyMessage topeMsg){
        if(-1 == topeMsg.getFixedHeader().getDeviceId()){
            //后门设备ID，不校验
            return true;
        }

        if(SysUtility.isEmpty(whyKernelProperties)){
            whyKernelProperties = WhySpringUtils.getBean(WhyKernelProperties.class);
        }
        String paramDevicePwd = new String(topeMsg.getPayload());
        if(SysUtility.isEmpty(paramDevicePwd)){
            WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep108.getCodeBytes()));
            WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep108.getCodeBytes(), LogPointCode.C11.getCode(), LogPointCode.C11.getMessage());
            return false;
        }
        //校验
        JSONObject params = new JSONObject();
        params.put("userId", topeMsg.getFixedHeader().getUserId());
        params.put("deviceId", topeMsg.getFixedHeader().getDeviceId());
        RemotingHttpResult rt = WhyCloudUtils.postBody(whyKernelProperties, RequestCode.Req23.getCode(), params, 1);
        if(TimerRunConstants.HTTP_CODE_SUCCESS == rt.getResponseCode()){
            JSONObject data = JSONObject.parseObject(rt.getResponseStr());
            String devicePwd = data.getString("data");
            if(SysUtility.isEmpty(devicePwd)){
                log.info("调用一体化平台 devicePwd 返回为空 失败...");
                WhyChannelUtils.writeAndFlush(ctx, WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep109.getCodeBytes()));
                WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep109.getCodeBytes(), LogPointCode.C12.getCode(), LogPointCode.C12.getMessage());
                return false;
            }
            if(!devicePwd.equals(paramDevicePwd)){
                WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep110.getCodeBytes()));
                WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep110.getCodeBytes(), LogPointCode.C13.getCode(), LogPointCode.C13.getMessage());
                return false;
            }
        }else if(TimerRunConstants.HTTP_CODE_FAIL == rt.getResponseCode()){
            log.info("调用一体化平台失败...");
            WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep109.getCodeBytes()));
            return false;
        }else{
            WhyChannelUtils.writeAndFlush(ctx,  WhyMessageFactory.newMessage(topeMsg, ResponseCode.Rep111.getCodeBytes()));
            WhyChannelUtils.c2pChannelForceClose(topeMsg, channelKey, ctx, ResponseCode.Rep111.getCodeBytes(), LogPointCode.C14.getCode(), LogPointCode.C14.getMessage());
            return false;
        }

        return true;
    }

    private void consumerOfflineSingle(WhyMessage connMsg){
        //获取当前用户的channel
        String channelKey = WhyChannelUtils.getCurrentC2pChannelKey(connMsg);
        Channel channel = WhyChannelUtils.getChannel(channelKey);

        //遍历当前用户缓存的个人离线消息
//        try {
//            String offlineSingleMsgKey = TopeChannelUtils.getOfflineSingleKeys(connMsg);
//            Set<String> keys = redisTemplate.keys(offlineSingleMsgKey);
//            for (String key : keys) {
//                try {
//                    TopeMessage topeMessage = (TopeMessage) redisTemplate.opsForValue().get(key);
//                    //发送离线消息
//                    sendOfflineMsg(channel, topeMessage);
//                } catch (Exception e) {
//                    log.error("离线消息发送失败:{}", e);
//                } finally {
//                    //删除redis
//                    redisTemplate.delete(key);
//                }
//            }
//
//            //遍历当前用户缓存的个人离线消息
//            String offlineAllMsgKey = TopeChannelUtils.getOfflineAllKeys(connMsg);
//            keys = redisTemplate.keys(offlineAllMsgKey);
//            for (String key : keys) {
//                Object value = redisTemplate.opsForValue().get(key);
//                TopeMessage topeMessage = (TopeMessage) SerializationUtils.deserialize((byte[]) value);
//
//                //跳过已经发送过的有效期内的广播消息
//                String oldKey = TopeChannelUtils.getOfflineAllAlreadyConsumerKey(topeMessage);
//                if (redisTemplate.hasKey(oldKey)) {
//                    continue;
//                }
//                //发送离线消息
//                sendOfflineMsg(channel, topeMessage);
//
//                //当前时间加上旧key的剩余时间，得到一个绝对时间
//                long expireTimestamp = System.currentTimeMillis() + redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
//                redisTemplate.opsForValue().set(oldKey, "");
//                redisTemplate.expireAt(oldKey, new Date(expireTimestamp));
//            }
//        } catch (Exception e) {
//            log.error("离线消息发送失败:{}", e);
//            throw new RuntimeException(e);
//        }
    }

    private void sendOfflineMsg(Channel channel, WhyMessage whyMessage){
        //是否剔除可变头
        channel.writeAndFlush(WhyMessageFactory.getClientTopeMessage(whyMessage).bytes());

        //如果该值为1，表示发送客户端的同时发送一份到服务端，0表示只发送客户端
        if(whyMessage.getVariableHeader().isTwoWayMsg()){
            String clusterKey = WhyChannelUtils.getCurrentS2pChannelKey(whyMessage);
            Channel c = WhyChannelUtils.getS2pCacheChannel(clusterKey, whyMessage, whyKernelProperties.getServerCacheChannelReTimes());
            if(SysUtility.isNotEmpty(c)){
                c.writeAndFlush(whyMessage.bytes());
            }
        }
    }
}
