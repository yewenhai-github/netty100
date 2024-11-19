package com.netty100.service;

import com.netty100.entity.ReportWarnStatisticsRate;

import java.util.List;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/18
 */
public interface ReportWarnStatisticsRateService {

    int insertBatch(List<ReportWarnStatisticsRate> list);

    List<ReportWarnStatisticsRate> listByClusterId(Integer clusterId);
}
