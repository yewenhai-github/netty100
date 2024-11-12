package com.netty100.kernel.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
@Data
public class ServerChannelIdleActive {
    String channelId;
    String channelKey;
    Date idleTime;
    String localAddress;
    String localPort;
    String remoteAddress;
    String remotePort;

    byte messageWay;
    byte messageSource;
    byte messageDest;
    byte messageType;
    byte messageSerialize;
}
