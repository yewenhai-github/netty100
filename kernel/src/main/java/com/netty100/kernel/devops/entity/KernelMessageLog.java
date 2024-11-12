package com.netty100.kernel.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author yewenhai
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
