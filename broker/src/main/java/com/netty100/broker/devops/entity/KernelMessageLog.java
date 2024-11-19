package com.netty100.broker.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author why
 * @version 1.0.0, 2022/4/25
 * @since 1.0.0, 2022/4/25
 */
@Data
public class KernelMessageLog {
    String logPoint;
    String logContent;
    Date logTime;
    String localAddress;
    Long userId;
    Long deviceId;
    byte messageSource;
    byte messageDest;

}
