package com.netty100.broker.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author why
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
@Data
public class ClientChannelExceptionCaught {
    String channelId;
    String channelKey;
    Date disConnectTime;
    Long userId;
}
