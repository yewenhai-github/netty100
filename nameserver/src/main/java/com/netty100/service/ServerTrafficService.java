package com.netty100.service;

import com.netty100.entity.ServerTraffic;
import com.netty100.pojo.vo.*;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface ServerTrafficService {

    int save(ServerTraffic serverTraffic);

    List<StatisticsServerTrafficVo> getStatisticsData(String date, List<Integer> clusterIds);

    void serverMessageRemove();

    void serverMessageThresholdMonitor();

    void updateRecordNotStatistics(Integer id);

    MessageQuality getMessageQualityByClusterIds(String date,List<Integer> clusterIds);

    TrafficConnectionFlowVo getMonitorData(Integer clusterId, Integer serverId, String date, String time);

    TrafficTotalVo getTotal(Integer serverId, String createDate);

    MessageRateVo getMessageRate(Integer clusterId, Integer serverId, String date, String time);

    List<ServerTrafficConnectionVo> getConnectionData(String date, Integer clusterId, Integer serverId);

    BigInteger getDayTotalTraffic(Integer clusterId, Integer serverId, String date);

    ForwardRateDetectionVo getForwardRateVo(Integer clusterId, String createDate, String createTime);
}
