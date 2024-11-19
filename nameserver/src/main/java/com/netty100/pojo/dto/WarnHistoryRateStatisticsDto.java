package com.netty100.pojo.dto;

import lombok.Data;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/18
 */
@Data
public class WarnHistoryRateStatisticsDto {
    String createDay;
    Integer clusterId;
    Integer count;
}
