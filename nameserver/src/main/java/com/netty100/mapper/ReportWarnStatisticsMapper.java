package com.netty100.mapper;


import com.netty100.entity.ReportWarnStatistics;
import com.netty100.pojo.dto.WarnHistoryRateStatisticsDto;
import com.netty100.pojo.vo.WarnIncreaseRate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReportWarnStatisticsMapper {

    int insertBatch(List<ReportWarnStatistics> list);

    List<WarnHistoryRateStatisticsDto> selectRateStatistics(@Param("timeList") List<String> timeList
            ,@Param("clusterIds") List<Integer> clusterIds);

    List<WarnIncreaseRate> getWarnIncreaseRate(@Param("clusterIds") List<Integer> clusterIds);
}