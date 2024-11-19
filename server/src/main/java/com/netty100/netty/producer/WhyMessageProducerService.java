package com.netty100.netty.producer;

import com.netty100.common.protocol.WhyMessageQoS;
import io.netty.channel.Channel;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public interface WhyMessageProducerService {

    /****************************轮询使用channel定位******************************/
    /**
     * 发送消息给集群内的管道对象（单条消息）
     * @param messageSource
     * @param messageDest
     * @param data 数据对象
     */
    boolean sendMessage(byte messageSource, byte messageDest, byte[] data);

    /****************************[单用户] ->发往游戏端管道方法，使用userId定位channel******************************/
    /**
     * 发送消息给单个用户
     * @param userId 用户ID
     * @param uri mappingId
     * @param data 数据对象
     */
    boolean sendMessage(Long userId, String uri, Object data);

    /**
     * 发送消息给单个用户
     * @param userId 用户ID
     * @param uri mappingId
     * @param data 数据对象
     * @param messageSource
     * @param messageDest
     */
    boolean sendMessage(Long userId, String uri, Object data, byte messageSource, byte messageDest);

    /**
     * 发送消息给单个用户
     * @param messageWay
     * @param messageSource
     * @param messageDest
     * @param messageSerialize
     * @param userId 用户ID
     * @param data 数据对象
     */
    boolean sendMessage(byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long userId, byte[] data);

    /**
     * 发送消息给单个用户
     * @param userId 用户ID
     * @param data 数据对象
     */
    boolean sendMessage(Long userId, byte[] data);
    /**
     * 发送消息给单个用户（web、job、mq服务都可使用）
     * @param channel 管道
     * @param userId 用户ID
     * @param data 数据对象
     */
    boolean sendMessage(Channel channel, Long userId, byte[] data);

    /**
     * 发送消息给单个用户
     * @param channel 管道
     * @param messageWay
     * @param messageSource
     * @param messageDest
     * @param messageSerialize
     * @param userId 用户ID
     * @param data 数据对象
     */
    boolean sendMessage(Channel channel, byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long userId, byte[] data);


    /****************************[多用户] ->发往游戏端管道方法，使用userId定位channel******************************/
    /**
     * 发送消息给多个用户
     * @param userIds 用户ID数组
     * @param data 数据对象
     */
    void sendMessage(Long[] userIds, byte[] data);
    /**
     * 发送消息给多个用户（web、job、mq服务都可使用）
     * @param channel 管道
     * @param userIds 用户ID数组
     * @param data 数据对象
     */
    void sendMessage(Channel channel, Long[] userIds, byte[] data);

    /**
     * 发送消息给多个用户
     * @param messageWay
     * @param messageSource
     * @param messageDest
     * @param messageSerialize
     * @param userIds 用户ID数组
     * @param data 数据对象
     */
    void sendMessage(byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long[] userIds, byte[] data);
    /**
     * 发送消息给多个用户
     * @param channel 管道
     * @param messageWay
     * @param messageSource
     * @param messageDest
     * @param messageSerialize
     * @param userIds 用户ID数组
     * @param data 数据对象
     */
    void sendMessage(Channel channel, byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long[] userIds, byte[] data);

    /****************************[所有用户] ->发往游戏端管道方法，使用userId定位channel******************************/
    /**
     * 发送消息给所有用户
     * @param userId 用户ID，只是用来判断下当前是“真实”用户触发
     * @param data 数据对象
     */
    boolean sendAllMessage(Long userId, byte[] data);
    /**
     * 发送消息给所有用户（web、job、mq服务都可使用）
     * @param channel 管道
     * @param userId 用户ID，触发的用户id
     * @param data 数据对象
     */
    boolean sendAllMessage(Channel channel, Long userId, byte[] data);

    /**
     * 发送消息给多个用户（web服务使用）
     * @param messageWay
     * @param messageSource
     * @param messageDest
     * @param messageSerialize
     * @param userId 用户ID
     * @param data 数据对象
     */
    boolean sendAllMessage(byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long userId, byte[] data);
    /**
     * 发送消息给多个用户
     * @param channel 管道
     * @param messageWay
     * @param messageSource
     * @param messageDest
     * @param messageSerialize
     * @param userId 用户ID
     * @param data 数据对象
     */
    boolean sendAllMessage(Channel channel, byte messageWay, byte messageSource, byte messageDest, byte messageSerialize, Long userId, byte[] data);

    boolean sendOfflineMessage(Long userId, String uri, Object data, int expiresTime);
    boolean sendOfflineMessage(boolean isFirstTime, Long userId, String uri, Object data, int expiresTime);
    boolean sendOfflineMessage(boolean isFirstTime, Long userId, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel);
    boolean sendOfflineMessage(boolean isFirstTime, Long userId, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel, byte messageSource, byte messageDest);
    boolean sendOfflineMessage(Long[] userIds, String uri, Object data, int expiresTime);
    boolean sendOfflineMessage(boolean isFirstTime, Long[] userIds, String uri, Object data, int expiresTime);
    boolean sendOfflineMessage(boolean isFirstTime, Long[] userIds, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel);
    boolean sendOfflineMessage(boolean isFirstTime, Long[] userIds, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel, byte messageSource, byte messageDest);
    boolean sendOfflineAllMessage(String uri, Object data, int expiresTime);
    boolean sendOfflineAllMessage(boolean isFirstTime, String uri, Object data, int expiresTime);
    boolean sendOfflineAllMessage(boolean isFirstTime, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel);
    boolean sendOfflineAllMessage(boolean isFirstTime, String uri, Object data, int expiresTime, WhyMessageQoS qosLevel, byte messageSource, byte messageDest);
}
