package com.netty100.mapper;

import com.netty100.entity.WarnHistoryInfo;
import com.netty100.pojo.dto.WarnStatisticsDto;
import com.netty100.pojo.vo.WarnIndexCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface WarnHistoryInfoMapper {

    int save(WarnHistoryInfo warnInfo);

    List<WarnStatisticsDto> getWarnInfoStatistics(@Param("time") String time);

    List<WarnIndexCount> getTypeCount(@Param("date") String date, @Param("clusterIds")List<Integer> clusterIds);

    List<WarnHistoryInfo> getWarnInfoList(@Param("clusterId") Integer clusterId, @Param("serverId") Integer serverId,@Param("date") String date);

    void batchDelete(List<Integer> collect);

    int updateByCreateDay(@Param("createDay") String createDay);

}
