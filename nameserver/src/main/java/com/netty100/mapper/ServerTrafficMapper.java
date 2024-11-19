package com.netty100.mapper;

import com.netty100.entity.ServerTraffic;
import com.netty100.pojo.dto.ServerTrafficDto;
import com.netty100.pojo.vo.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface ServerTrafficMapper {

    int save(ServerTraffic serverTraffic);

    List<StatisticsServerTrafficVo> getStatisticsData(@Param("date") String date, @Param("clusterIds") List<Integer> clusterIds);

    List<Integer> queryRemoveRecordIds(@Param("removeDay") String removeDay, @Param("pageSize") Integer pageSize);

    void clearMessage(@Param("ids") List<Integer> ids);

    List<ServerTrafficDto> queryServerMessageByCreateTime(@Param("serverId") Integer serverId,
                                                          @Param("createDate") String createDate, @Param("timeList") List<String> timeList);

    void updateRecordNotStatistics(Integer id);

    MessageQuality getMessageQualityByClusterIds(@Param("date") String date, @Param("clusterIds") List<Integer> clusterIds);

    TrafficConnectionFlowVo getMonitorData(@Param("clusterId") Integer clusterId, @Param("serverId") Integer serverId, @Param("date") String date, @Param("time") String time);

    TrafficTotalVo getTotal(@Param("serverId") Integer serverId, @Param("createDate") String createDate);

    MessageRateVo getMessageRate(@Param("clusterId") Integer clusterId, @Param("serverId") Integer serverId, @Param("date") String date, @Param("time") String time);

    List<ServerTrafficConnectionVo> getConnectionData(@Param("date") String date, @Param("clusterId") Integer clusterId, @Param("serverId") Integer serverId);

    BigInteger getDayTotalTraffic(@Param("clusterId") Integer clusterId, @Param("serverId") Integer serverId, @Param("date") String date);

    ForwardRateDetectionVo getForwardRateVo(@Param("clusterId") Integer clusterId, @Param("createDate") String createDate, @Param("createTime") String createTime);
}
