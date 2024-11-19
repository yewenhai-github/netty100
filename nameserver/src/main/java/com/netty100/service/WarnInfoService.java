package com.netty100.service;


import com.netty100.entity.WarnHistoryInfo;
import com.netty100.pojo.vo.WarnIncreaseRate;
import com.netty100.pojo.vo.WarnIndexCount;
import com.netty100.pojo.vo.WarnTypeCount;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface WarnInfoService {

    int add(WarnHistoryInfo warnInfo);

    void clusterWarnStatistics();

    List<WarnTypeCount> getTypeCount(String date, List<Integer> clusterIds);

    List<WarnHistoryInfo> getWarnInfoList(Integer clusterId, Integer serverId,String date);

    List<WarnIncreaseRate> getWarnIncreaseRate(List<Integer> clusterIds);
}
