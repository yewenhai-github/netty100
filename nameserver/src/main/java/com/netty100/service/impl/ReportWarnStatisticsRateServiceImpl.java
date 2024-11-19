package com.netty100.service.impl;

import com.netty100.entity.ReportWarnStatisticsRate;
import com.netty100.mapper.ReportWarnStatisticsRateMapper;
import com.netty100.service.ReportWarnStatisticsRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/18
 */
@Service
public class ReportWarnStatisticsRateServiceImpl implements ReportWarnStatisticsRateService {
    @Autowired
    private ReportWarnStatisticsRateMapper reportWarnStatisticsRateMapper;
    @Override
    public int insertBatch(List<ReportWarnStatisticsRate> list) {
        return reportWarnStatisticsRateMapper.insertBatch(list);
    }

    @Override
    public List<ReportWarnStatisticsRate> listByClusterId(Integer clusterId) {
        return reportWarnStatisticsRateMapper.listByClusterId(clusterId);
    }
}
