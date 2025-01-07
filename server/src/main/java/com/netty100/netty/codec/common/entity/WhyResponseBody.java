package com.netty100.netty.codec.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Data
public class WhyResponseBody<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 转发路径
     */
    private String uri;

    /**
     * 报文体
     */
    private byte[] data;

    /**
     * code
     */
    private String code;

    /**
     * 通知消息
     */
    private String message;

}
