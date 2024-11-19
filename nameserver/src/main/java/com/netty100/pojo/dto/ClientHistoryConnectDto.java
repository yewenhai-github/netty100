package com.netty100.pojo.dto;

import lombok.Data;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/15
 */
@Data
public class ClientHistoryConnectDto {
    private Long userId;
    private Integer serverId;
    private Integer clusterId;
    private Integer actionType;
    private String occurTime;
}
