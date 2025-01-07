package com.netty100.netty.producer;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.netty100.common.protocol.*;
import com.netty100.netty.codec.common.entity.WhyResponseBody;
import com.netty100.netty.codec.common.entity.WhyResponseBodyProto2;
import com.netty100.netty.codec.common.entity.WhyResponseBodyProto3;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.properties.WhyNettyCommonProperties;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.server.connect.ClientReconnect;
import com.netty100.netty.server.properties.WhyNettyServerProperties;
import com.netty100.netty.server.utils.WhyServerUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
@Service("whyMessageProducerServiceImpl")
public class WhyMessageProducerServiceImpl implements WhyMessageProducerService {
    @Autowired
    private WhyNettyCommonProperties whyNettyCommonProperties;
    @Autowired
    private WhyNettyServerProperties whyNettyServerProperties;
    @Autowired
    private ClientReconnect clientReconnect;


    /****************************轮询使用channel定位******************************/
    @Override
    public boolean sendMessage(byte messageSource, byte messageDest, byte[] data) {
        return sendMessage(WhyServerUtils.getCurrentChannel(), CommonConstants.way_simplex_channelRead0, messageSource, messageDest, CommonConstants.DEFAULT_MESSAGE_SERIALIZE, 0L, data);
    }

    /****************************[单用户] ->发往游戏端管道方法，使用userId定位channel******************************/
    public boolean sendMessage(Long userId, String uri, Object data){
        return sendMessage(userId, convertData(uri, data));
    }

    public boolean sendMessage(Long userId, String uri, Object data, byte messageSource, byte messageDest){
        return sendMessage(CommonConstants.way_s2p_channelRead0, messageSource, messageDest, CommonConstants.DEFAULT_MESSAGE_SERIALIZE, userId, convertData(uri, data));
    }

    @Override
    public boolean sendMessage(byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long userId,byte[] data) {
        return sendMessage(WhyServerUtils.getCurrentChannel(), messageWay, messageSource, messageDest, messageSerialize, userId, data);
    }

    @Override
    public boolean sendMessage(Long userId,byte[] data) {
        return sendMessage(WhyServerUtils.getCurrentChannel(), userId, data);
    }

    @Override
    public boolean sendMessage(Channel channel, Long userId,byte[] data) {
        return sendMessage(channel, CommonConstants.way_s2p_channelRead0, CommonConstants.DEFAULT_MESSAGE_SOURCE, CommonConstants.DEFAULT_MESSAGE_DEST, CommonConstants.DEFAULT_MESSAGE_SERIALIZE, userId, data);
    }

    @Override
    public boolean sendMessage(Channel channel, byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long userId,byte[] data) {
        if(SysUtility.isEmpty(userId)){
            log.info("sendOnlineMessageSingle：用户ID不能为空");
            return false;
        }
        return sendOnlineMessageNative(channel, messageWay, messageSource, messageDest, WhyMessageCode.type_online_single_message.getCode(), messageSerialize, userId, data);
    }
    /****************************[多用户] ->发往游戏端管道方法，使用userId定位channel******************************/
    @Override
    public void sendMessage(Long[] userIds,byte[] data) {
        sendMessage(WhyServerUtils.getCurrentChannel(), userIds, data);
    }

    @Override
    public void sendMessage(Channel channel, Long[] userIds, byte[] data) {
        sendMessage(channel, CommonConstants.way_s2p_channelRead0, CommonConstants.DEFAULT_MESSAGE_SOURCE, CommonConstants.DEFAULT_MESSAGE_DEST, CommonConstants.DEFAULT_MESSAGE_SERIALIZE, userIds, data);
    }

    @Override
    public void sendMessage(byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long[] userIds,byte[] data) {
        sendMessage(WhyServerUtils.getCurrentChannel(), messageWay, messageSource, messageDest, messageSerialize, userIds, data);
    }

    @Override
    public void sendMessage(Channel channel, byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long[] userIds,byte[] data) {
        Arrays.stream(userIds).forEach(userId ->{
            sendOnlineMessageNative(channel, messageWay, messageSource, messageDest, CommonConstants.DEFAULT_MESSAGE_TYPE, messageSerialize, userId, data);
        });
    }

    /****************************[所有用户] ->发往游戏端管道方法，使用userId定位channel******************************/
    @Override
    public boolean sendAllMessage(Long userId, byte[] data) {
        return sendAllMessage(WhyServerUtils.getCurrentChannel(), userId, data);
    }

    @Override
    public boolean sendAllMessage(Channel channel, Long userId,byte[] data) {
        return sendAllMessage(channel, CommonConstants.way_s2p_channelRead0, CommonConstants.DEFAULT_MESSAGE_SOURCE, CommonConstants.DEFAULT_MESSAGE_DEST, CommonConstants.DEFAULT_MESSAGE_SERIALIZE, userId, data);
    }

    @Override
    public boolean sendAllMessage(byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long userId,byte[] data) {
        return sendOnlineMessageNative(WhyServerUtils.getCurrentChannel(), messageWay, messageSource, messageDest, WhyMessageCode.type_online_all_message.getCode(), messageSerialize, userId, data);
    }

    @Override
    public boolean sendAllMessage(Channel channel, byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long userId,byte[] data) {
        return sendOnlineMessageNative(channel, messageWay, messageSource, messageDest, WhyMessageCode.type_online_all_message.getCode(), messageSerialize, userId, data);
    }

    /****************************离线消息发送方法******************************/
    @Override
    public boolean sendOfflineMessage(Long userId, String uri, Object data, int expiresTime) {
        return sendOfflineMessage(false, userId, uri, data, expiresTime, WhyMessageQoS.AT_MOST_ONCE);
    }
    @Override
    public boolean sendOfflineMessage(boolean isFirstTime, Long userId, String uri, Object data, int expiresTime) {
        return sendOfflineMessage(isFirstTime, userId, uri, data, expiresTime, WhyMessageQoS.AT_MOST_ONCE);
    }
    @Override
    public boolean sendOfflineMessage(boolean isFirstTime, Long userId, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel) {
        return sendOfflineMessage(isFirstTime, userId, uri, data, expiresTime, qosLevel, CommonConstants.DEFAULT_MESSAGE_SOURCE, CommonConstants.DEFAULT_MESSAGE_DEST);
    }
    @Override
    public boolean sendOfflineMessage(boolean isFirstTime, Long userId, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel, byte messageSource, byte messageDest) {
        return sendOfflineMessageNative(CommonConstants.way_s2p_channelRead0, messageSource, messageDest, WhyMessageCode.type_offline_single_message.getCode(),
                CommonConstants.DEFAULT_MESSAGE_SERIALIZE, userId, convertData(uri, data), expiresTime, qosLevel);
    }
    @Override
    public boolean sendOfflineMessage(Long[] userIds, String uri, Object data, int expiresTime) {
        Arrays.stream(userIds).forEach(userId -> sendOfflineMessage(false, userId, uri, data, expiresTime));
        return true;
    }
    @Override
    public boolean sendOfflineMessage(boolean isFirstTime, Long[] userIds, String uri, Object data, int expiresTime) {
        Arrays.stream(userIds).forEach(userId -> sendOfflineMessage(isFirstTime, userId, uri, data, expiresTime));
        return true;
    }
    @Override
    public boolean sendOfflineMessage(boolean isFirstTime, Long[] userIds, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel) {
        Arrays.stream(userIds).forEach(userId -> sendOfflineMessage(isFirstTime, userId, uri, data, expiresTime, qosLevel));
        return true;
    }
    @Override
    public boolean sendOfflineMessage(boolean isFirstTime, Long[] userIds, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel, byte messageSource, byte messageDest) {
        Arrays.stream(userIds).forEach(userId -> sendOfflineMessage(isFirstTime, userId, uri, data, expiresTime, qosLevel, messageSource, messageDest));
        return true;
    }

    @Override
    public boolean sendOfflineAllMessage(String uri, Object data, int expiresTime) {
        return sendOfflineAllMessage(false, uri, data, expiresTime, WhyMessageQoS.AT_MOST_ONCE, CommonConstants.DEFAULT_MESSAGE_SOURCE, CommonConstants.DEFAULT_MESSAGE_DEST);
    }
    @Override
    public boolean sendOfflineAllMessage(boolean isFirstTime, String uri, Object data, int expiresTime) {
        return sendOfflineAllMessage(isFirstTime, uri, data, expiresTime, WhyMessageQoS.AT_MOST_ONCE, CommonConstants.DEFAULT_MESSAGE_SOURCE, CommonConstants.DEFAULT_MESSAGE_DEST);
    }
    @Override
    public boolean sendOfflineAllMessage(boolean isFirstTime, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel) {
        return sendOfflineAllMessage(isFirstTime, uri, data, expiresTime, qosLevel, CommonConstants.DEFAULT_MESSAGE_SOURCE, CommonConstants.DEFAULT_MESSAGE_DEST);
    }
    @Override
    public boolean sendOfflineAllMessage(boolean isFirstTime, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel, byte messageSource, byte messageDest) {
        return sendOfflineMessageNative(CommonConstants.way_s2p_channelRead0, messageSource, messageDest, WhyMessageCode.type_offline_all_message.getCode(),
                CommonConstants.DEFAULT_MESSAGE_SERIALIZE, CommonConstants.DEFAULT_USER_ID, convertData(uri, data), expiresTime, qosLevel);
    }

    /****************************本地方法，提供发送单用户的思路，为单用户、多用户、全用户等三套接口使用******************************/
    private boolean sendOnlineMessageNative(Channel channel, byte messageWay, byte messageSource, byte messageDest, byte messageType, byte messageSerialize, Long userId, byte[] data){
        return sendMessageNative(messageWay, messageSource, messageDest, messageType, messageSerialize, userId,
                data,
                CommonConstants.DEFAULT_VARIABLE_API_VERSION, CommonConstants.DEFAULT_IS_TWO_WAY_MSG, CommonConstants.DEFAULT_IS_FIRST_TIME, WhyMessageQoS.AT_MOST_ONCE, CommonConstants.DEFAULT_VARIABLE_IS_REWRITE, CommonConstants.DEFAULT_EXPIRES_TIME);
    }

    private boolean sendOfflineMessageNative(byte messageWay, byte messageSource, byte messageDest, byte messageType, byte messageSerialize, Long userId, byte[] data, int expiresTime, WhyMessageQoS qosLevel){
        return sendMessageNative(messageWay, messageSource, messageDest, messageType, messageSerialize, userId,
                data,
                CommonConstants.DEFAULT_VARIABLE_API_VERSION, CommonConstants.DEFAULT_IS_TWO_WAY_MSG, CommonConstants.DEFAULT_IS_FIRST_TIME, qosLevel, CommonConstants.DEFAULT_VARIABLE_IS_REWRITE, expiresTime);
    }




    private boolean sendMessageNative(byte messageWay, byte messageSource, byte messageDest, byte messageType, byte messageSerialize, Long userId,
                                      byte[] data,
                                      byte apiVersion, boolean isTwoWayMsg, boolean isFirstTime, WhyMessageQoS qosLevel, boolean isRewrite, int expiresTime){
        if (userId == null) {
            log.info("用户Id={},不能为空", userId);
            return false;
        }

        //固定头参数配置读取处理
        Long messageId = SysUtility.isNotEmpty(SysUtility.getCurrentClientMessageId()) ? SysUtility.getCurrentClientMessageId() : WhyMessageFactory.createMessageId();
        messageSource = (CommonConstants.DEFAULT_MESSAGE_SOURCE == messageSource) ? whyNettyCommonProperties.getMessageSource() : messageSource;
        messageDest = (CommonConstants.DEFAULT_MESSAGE_DEST == messageDest) ? whyNettyCommonProperties.getMessageDest() : messageDest;
        messageSerialize = (CommonConstants.DEFAULT_MESSAGE_SERIALIZE == messageSerialize) ? whyNettyCommonProperties.getMessageSerialize() : messageSerialize;
        messageWay = (CommonConstants.DEFAULT_MESSAGE_WAY == messageWay) ? whyNettyCommonProperties.getMessageWay() : messageWay;

        //可变头参数配置读取处理，优先取配置文件，之后再是入参
        boolean isLeaf = SysUtility.isEmpty(SysUtility.getCurrentClientMessageId()) ? false : true;
        apiVersion = (whyNettyCommonProperties.getApiVersion() != apiVersion) ? whyNettyCommonProperties.getApiVersion() : apiVersion;
        isTwoWayMsg = (whyNettyCommonProperties.isTwoWayMsg() != isTwoWayMsg) ? whyNettyCommonProperties.isTwoWayMsg() : isTwoWayMsg;
        isFirstTime = (whyNettyCommonProperties.isFirstTime() != isFirstTime) ? whyNettyCommonProperties.isFirstTime() : isFirstTime;
        qosLevel = (WhyMessageQoS.valueOf(whyNettyCommonProperties.getQosLevel()) != qosLevel) ? WhyMessageQoS.valueOf(whyNettyCommonProperties.getQosLevel()) : qosLevel;
        isRewrite = (whyNettyCommonProperties.isRewrite() != isRewrite) ? whyNettyCommonProperties.isRewrite() : isRewrite;

        //构造消息
        WhyMessageFixedHeader fixedHeader = WhyMessageFactory.newFixedHeader(messageWay, messageSource, messageDest, messageType, messageSerialize, messageId, CommonConstants.DEFAULT_DEVICE_ID, userId, true);
        WhyMessageVariableHeader variableHeader = WhyMessageFactory.newVariableHeader(apiVersion, isLeaf, isTwoWayMsg, isFirstTime, false, qosLevel, isRewrite, expiresTime);
        WhyMessage whyMsg = WhyMessageFactory.newMessage(fixedHeader, variableHeader, data);

        //消息发送
        return send(whyMsg, userId, whyNettyServerProperties.getMessageSendRetry(), false);
    }

    //TODO isDup变量功能暂不实现，与qos一同实现
    private boolean send(WhyMessage whyMsg, Long userId, int messageSendRetry, boolean isDup){
        Channel channel = WhyServerUtils.getCurrentChannel();
        try {
            if(SysUtility.isEmpty(channel)){
                log.error("用户{}消息发送失败,重试次数{},管道为空，ip={}", userId, whyNettyServerProperties.getMessageSendRetry() - messageSendRetry, SysUtility.getHostIp());
                return false;
            }
            channel.writeAndFlush(whyMsg.bytes());
            return true;
        } catch (Exception e) {
            if(messageSendRetry <= 0){
                log.info("用户{}消息发送失败,重试次数{},已达到消息重发次数上限", userId, whyNettyServerProperties.getMessageSendRetry() - messageSendRetry);
                return false;
            }

            messageSendRetry  -= messageSendRetry;
            log.warn("用户{}消息发送失败,第{}次重发,管道已关闭", userId, (whyNettyServerProperties.getMessageSendRetry() - messageSendRetry), e);

            //关闭连接连接
            if(SysUtility.isNotEmpty(channel)){
                WhyServerUtils.closeCtx(channel);
                //触发重选机制，补全缺失的连接
                clientReconnect.doCommand();
            }

            //递归消息重发
            return send(whyMsg, userId, messageSendRetry, isDup);
        }
    }

    private byte[] convertData(String uri, Object data){
        if(data instanceof byte[]){
            return (byte[]) data;
        }

        try {
            byte messageSerialize = (SysUtility.isNotEmpty(whyNettyCommonProperties.getMessageSerialize())) ? whyNettyCommonProperties.getMessageSerialize() : CommonConstants.DEFAULT_MESSAGE_SERIALIZE;
            switch (messageSerialize){
                case 1:
                    return ((String)data).getBytes(StandardCharsets.UTF_8);
                case 2:
                    WhyResponseBodyProto2.ResponseBody.Builder builder2 = WhyResponseBodyProto2.ResponseBody.newBuilder();
                    if (data != null) {
                        ByteString bytes = ((AbstractMessageLite) data).toByteString();
                        builder2.setData(bytes);
                    }
                    builder2.setUrl(uri);
                    builder2.setCode("200");//HttpStatus.HTTP_OK
                    builder2.setMessage("");
                    return builder2.build().toByteArray();
                case 3:
                    WhyResponseBodyProto3.ResponseBody.Builder builder3 = WhyResponseBodyProto3.ResponseBody.newBuilder();
                    if (data != null) {
                        ByteString bytes = ((AbstractMessageLite) data).toByteString();
                        builder3.setData(bytes);
                    }
                    builder3.setUrl(uri);
                    builder3.setCode("200");//HttpStatus.HTTP_OK
                    builder3.setMessage("");
                    return builder3.build().toByteArray();
                case 4:
                    JSONObject responseJson = new JSONObject();
                    responseJson.put("code", "200");
                    responseJson.put("message", "");
                    responseJson.put("uri", uri);
                    responseJson.put("data", data);
                    return responseJson.toJSONString().getBytes(StandardCharsets.UTF_8);
                case 5:
                    WhyResponseBody whyRequestBody = new WhyResponseBody();
                    whyRequestBody.setUri(uri);
                    whyRequestBody.setCode("200");
                    whyRequestBody.setMessage("");
                    whyRequestBody.setData(SerializationUtils.serialize(data));
                    return SerializationUtils.serialize(whyRequestBody);
                default:
                    throw new RuntimeException("不支持的序列化类型：" + messageSerialize);
            }
        } catch (RuntimeException e) {
            log.error("数据转换异常", e);
            throw new RuntimeException(e);
        }
    }


}
