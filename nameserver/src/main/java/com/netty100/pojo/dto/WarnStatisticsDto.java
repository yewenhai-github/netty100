package com.netty100.pojo.dto;

import lombok.Data;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/31
 */
@Data
public class WarnStatisticsDto {
    private Integer clusterId;
//    private Integer warnType;
    private Integer count;
}
