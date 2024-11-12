package com.netty100.netty.client.service;

import com.netty100.common.protocol.WhyMessageFixedHeader;
import com.netty100.common.service.TopeMessageService;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public interface WhyNettySdkService<I> extends TopeMessageService<I> {

    /**
     * 消息处理接口
     */
    default void doCommand(byte[] msg, WhyMessageFixedHeader header){

    }

}
