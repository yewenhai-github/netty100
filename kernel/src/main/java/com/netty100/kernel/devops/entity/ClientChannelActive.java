package com.netty100.kernel.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
@Data
public class ClientChannelActive {
    String channelId;
    String channelKey;
    Date connectTime;
    Long deviceId;
    String remoteIp;
    Long userId;

    byte messageWay;
    byte messageSource;
    byte messageDest;
    byte messageType;
    byte messageSerialize;
}
