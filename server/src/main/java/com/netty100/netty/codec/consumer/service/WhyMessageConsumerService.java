package com.netty100.netty.codec.consumer.service;

/**
 * @author why
 * @version 1.0.0, 2022/3/28
 * @since 1.0.0, 2022/3/28
 */
public interface WhyMessageConsumerService<T> {

    default void doCommand(T data, Long userId){

    }

}
