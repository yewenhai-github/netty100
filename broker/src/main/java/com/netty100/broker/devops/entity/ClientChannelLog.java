package com.netty100.broker.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author why
 * @version 1.0.0, 2022/5/31
 * @since 1.0.0, 2022/5/31
 */
@Data
public class ClientChannelLog {

    String logPoint;
    String logContent;
    Date logTime;
    String localAddress;
    String localPort;
    String remoteAddress;
    String remotePort;
    Long userId;
    byte messageSource;
    byte messageDest;
}
