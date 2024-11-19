package com.netty100.service;

import com.netty100.pojo.dto.StatisticsQueryDto;
import com.netty100.pojo.vo.StatisticsVo;

/**
 * @author why
 */
public interface StatisticalAnalysisService {

    StatisticsVo getStatisticsData(StatisticsQueryDto dto);
}
