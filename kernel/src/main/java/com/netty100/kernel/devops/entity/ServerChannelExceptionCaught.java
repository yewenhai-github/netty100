package com.netty100.kernel.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
@Data
public class ServerChannelExceptionCaught {
    String channelId;
    String channelKey;
    Date disConnectTime;
}
