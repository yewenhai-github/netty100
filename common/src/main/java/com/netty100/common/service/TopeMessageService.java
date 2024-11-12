package com.netty100.common.service;

import com.netty100.common.protocol.WhyMessageFixedHeader;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public interface TopeMessageService<I> {

    /**
     * 消息处理接口
     */
    default void doCommand(byte[] msg, WhyMessageFixedHeader header){

    }

}
