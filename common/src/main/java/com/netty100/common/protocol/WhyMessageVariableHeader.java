package com.netty100.common.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author why
 * @version 1.0.4, 2023/6/8
 * @since 1.0.4, 2023/6/8
 */
@Data
public class WhyMessageVariableHeader implements Serializable {
    //[1-2]字节 可变头的长度（可变头的字段，可以动态添加与删除，内核逻辑支持对应的位解析）
    short variableLength;
    //[3]字节 可变头的版本号，可以支持多个版本
    byte apiVersion;
    //[4]字节 Bit[7]为保留字段

    //[4]字节 Bit[6]日志的节点，0主节点 1叶子节点，[只有sdk触发的消息才需要区分叶子，属于第一次消息]
    boolean isLeaf;
    //[4]字节 Bit[5]如果该值为1，表示发送客户端的同时发送一份到服务端，0表示只发送客户端
    boolean isTwoWayMsg;
    //[4]字节 Bit[4]如果该值为1，表示如果用户在线则直接发送，0表示下一次用户登录时发送
    boolean isFirstTime;
    //[4]字节 Bit[3]如果该值为1，表明这个数据包是一条重复的消息；否则该数据包就是第一次发布的消息。
    boolean isDup;
    //[4]字节 Bit[2-1]为Qos字段：Bit1和Bit2为0表示QoS 0：至多一次；Bit1为1表示QoS1：至少一次；Bit2 为1表示QoS 2：只有一次；
    WhyMessageQoS qosLevel;
    //[4]字节 Bit[0] 是否剔除可变头
    boolean isRewrite;
    //[5-8]字节 离线消息有效时间，单位默认（秒）
    int expiresTime;
}
