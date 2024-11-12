package com.netty100.kernel.devops.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/25
 * @since 1.0.0, 2022/4/25
 */
@Data
public class ClientMessageLog {
    String logPoint;
    String logContent;
    String logTime;
    String localAddress;
    String localPort;
    String remoteAddress;
    String remotePort;
    Long userId;
    Long messageId;
    byte messageType;
    byte messageWay;
    byte messageSerialize;
    byte messageSource;
    byte messageDest;
    Long deviceId;
    //0主节点 1叶子节点
    Integer leaf = 1;
}
