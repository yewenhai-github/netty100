package com.netty100.common.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author why
 * @version 1.0.0, 2022/3/30
 * @since 1.0.0, 2022/3/30
 */
@Data
public class WhyMessageFixedHeader implements Serializable {
    /***
     * 消息事件监听状态位（1个字节）
     * 1：c2p-channelActive 用户端管道激活（客户端1）
     * 3：c2s-channelRead0 用户端消息报文（客户端1）
     * 5：c2p-idleState 用户端心跳报文（客户端1）
     * 7：c2p-channelInactive 退出报文（客户端1）
     * 9：c2p-exceptionCaught 异常报文（客户端1）
     *
     * 2：s2p-channelActive 后端管道激活（客户端2）
     * 4：s2c-channelRead0 后端消息报文（客户端2）
     * 6：s2p-idleState 后端心跳报文（客户端2）
     * 8：s2p-channelInactive 退出报文（客户端2）
     * 10：s2p-exceptionCaught 异常报文（客户端2）
     *
     * 16: simplex-channelActive 纯后端管道激活（客户端3）、way_m2p_idleState
     * 17：simplex-channelRead0 纯后端消息报文（客户端3）
     * 18：simplex-idleState 纯后端心跳报文（客户端3）
     * 19：simplex-channelInactive 退出报文（客户端3）
     * 20：simplex-exceptionCaught 异常报文（客户端3）
     *
     * 11: j2p_channelActive
     * 12：m2p_channelActive
     * 13：（未使用）
     * 14：（未使用）
     * 15：way_j2p_idleState
     * **/
    byte messageWay;
    /***
     *客户端来源（1字节），客户端应用唯一标识 1-Math 2-English 3-Chinese
     */
    byte messageSource;
    /***
     *服务器来源（1字节），后端业务集群唯一标识 1-Math 2-English 3-Chinese
     */
    byte messageDest;
    /***
     * 消息类型标志位（1字节）
     * 0平台接收
     * 1在线单用户，本地消息直接发送
     * 2在线全用户，本地消息直接发送,触发广播发送方式：循环本机channelMap+内部转发
     * 3离线单用户，暂不实现
     * 4离线全用户，暂不实现
     * 5在线全用户广播消费（和2配套使用）
     * 6在线单用户-定点转发（和1配套使用，如果定点发送失败，需清除错误的redis地址，并丢弃本次消息）
     * 7在线单用户-群发转发（和1配套使用，redis找不到addr，群发后只有消费成功的channel，将对应的addr记录到redis中）
     * 8平台客户端消息踢人下线内部报文
     */
    byte messageType;
    /***
     * 消息序列化格式（1字节）：
     * 1-string（平台消息报文必须传1）
     * 2-protobuf2
     * 3-protobuf3
     * 4-json
     * 5-java bean
     */
    byte messageSerialize;
    /**消息主键（8个字节），本次消息的唯一值，精确到纳秒*/
    Long messageId;
    /**设备Id（8个字节）：设备唯一标识*/
    Long deviceId;
    /**消息用户（8个字节），用户Id主键*/
    Long userId = -1L;
    // 是否有可变头
    boolean variableFlag = false;
}
