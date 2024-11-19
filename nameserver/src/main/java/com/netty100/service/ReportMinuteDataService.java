package com.netty100.service;

import com.netty100.entity.ReportMinuteData;
import com.netty100.pojo.vo.*;

import java.util.List;

/**
 * @author why
 */
public interface ReportMinuteDataService {

    ReportMinuteData getOne(String createDate, String createTime, Integer serverId);

    int save(ReportMinuteData entity);

    int updateById(ReportMinuteData entity);

    ReportMinuteData getLatest(Integer serverId);

    List<TpsQpsMinuteTotalVo> getByClusterIds(String date, List<Integer> clusterIds);

    void serverReconnectCheck();

    ForwardStatus getForwardStatus(Integer clusterId, Integer serverId, String date, String time);

    void serverReconnectCalc(String time, String fiveMinuteAgoTime, String oneMinuteAgoTime);

    MonitorTps getMonitorTps(Integer clusterId, Integer serverId, String date, String time);

    MonitorTpsQpsPeak getMonitorTpsQpsPeak(Integer clusterId, Integer serverId, String date);

    void saveConnectionCount(ReportMinuteData entity);

    void saveTrafficFlowStatus(ReportMinuteData data);
}
