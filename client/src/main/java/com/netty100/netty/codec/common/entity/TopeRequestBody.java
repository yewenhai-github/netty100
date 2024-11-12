package com.netty100.netty.codec.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Data
public class TopeRequestBody<T> implements Serializable {
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
     * token
     */
    private String token;
}
