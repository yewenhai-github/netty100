package com.netty100.broker.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author why
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
@Data
public class ClientChannelIdleActive {
    String channelId;
    String channelKey;
    Date idleTime;
    Long deviceId;
    String localAddress;
    String localPort;
    String remoteAddress;
    String remotePort;
    Long userId;

    byte messageWay;
    byte messageSource;
    byte messageDest;
    byte messageType;
    byte messageSerialize;
}
