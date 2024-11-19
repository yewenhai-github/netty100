package com.netty100.enumeration;

/**
 * 消息协议分类
 *
 * @author why
 */
public interface ProtocolType {

    /**
     * 消息类型
     */
    String MESSAGE_TYPE = "消息类型";

    /**
     * 消息源
     */
    String MESSAGE_SOURCE = "客户端来源";

    /**
     * 消息目的地
     */
    String MESSAGE_DEST = "服务器来源";

    /**
     * 消息方式
     */
    String MESSAGE_WAY = "消息方式";

    /**
     * 消息序列化方式
     */
    String MESSAGE_SERIALIZE = "消息序列化方式";
}
