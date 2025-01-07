package com.netty100.netty.server.service;

import com.netty100.common.protocol.WhyMessageFixedHeader;
import com.netty100.common.service.WhyMessageService;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public interface WhyNettySdkService<I> extends WhyMessageService<I> {

    /**
     * 消息处理接口
     */
    default void doCommand(byte[] msg, WhyMessageFixedHeader header){

    }

}
