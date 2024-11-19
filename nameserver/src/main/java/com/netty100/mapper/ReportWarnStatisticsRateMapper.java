package com.netty100.mapper;

import com.netty100.entity.ReportWarnStatisticsRate;

import java.util.List;

public interface ReportWarnStatisticsRateMapper {
    int insertBatch(List<ReportWarnStatisticsRate> list);

    List<ReportWarnStatisticsRate> listByClusterId(Integer clusterId);
}