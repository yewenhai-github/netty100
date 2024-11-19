package com.netty100.config.websocket;

import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
public class MessageVo<T> {

    private String messageType;

    private T result;

    public MessageVo(String messageType, T result) {
        this.messageType = messageType;
        this.result = result;
    }
}
