package com.netty100.mapper;

import com.netty100.entity.ReportMinuteData;
import com.netty100.pojo.vo.*;
import com.netty100.pojo.dto.ReportMinuteDataDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author why
 */
public interface ReportMinuteDataMapper {

    ReportMinuteData getOne(@Param("createDate") String createDate, @Param("createTime") String createTime, @Param("serverId") Integer serverId);

    int save(ReportMinuteData reportMinuteData);

    int updateById(ReportMinuteData entity);

    ReportMinuteData getLatest(Integer serverId);

    List<TpsQpsMinuteTotalVo> getByClusterIds(@Param("date") String date, @Param("clusterIds") List<Integer> clusterIds);

    ReportMinuteDataDto getAvgStatisticsByCreateTime(@Param("serverId") Integer serverId, @Param("date") String date, @Param("times") List<String> times);

    ForwardStatus getForwardStatus(@Param("clusterId") Integer clusterId, @Param("serverId") Integer serverId, @Param("date") String date, @Param("time") String time);

    MonitorTps getMonitorTps(@Param("clusterId") Integer clusterId, @Param("serverId") Integer serverId, @Param("date") String date, @Param("time") String time);

    MonitorTpsQpsPeak getMonitorTpsQpsPeak(@Param("clusterId") Integer clusterId, @Param("serverId") Integer serverId, @Param("date") String date);

    void saveConnectionCount(ReportMinuteData entity);

    void saveTrafficFlowStatus(ReportMinuteData data);

    void saveClientReconnectTimes(ReportMinuteData minuteErrorData);
}
