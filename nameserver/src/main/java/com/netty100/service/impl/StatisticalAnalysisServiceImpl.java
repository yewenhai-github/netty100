package com.netty100.service.impl;

import com.netty100.entity.User;
import com.netty100.enumeration.UserType;
import com.netty100.pojo.dto.StatisticsQueryDto;
import com.netty100.pojo.vo.*;
import com.netty100.service.*;
import com.netty100.utils.MathUtil;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author why
 */
@SuppressWarnings("DuplicatedCode")
@Service
public class StatisticalAnalysisServiceImpl implements StatisticalAnalysisService {

    private ReportMinuteDataService reportMinuteDataService;

    private ServerTrafficService serverTrafficService;

    private ClusterService clusterService;

    private HistoricalUserLocationService historicalUserLocationService;

    private ActiveUserLocationService activeUserLocationService;

    private UserService userService;

    private WarnInfoService warnInfoService;

    @Autowired
    public void setWarnInfoService(WarnInfoService warnInfoService) {
        this.warnInfoService = warnInfoService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Autowired
    public void setReportMinuteDataService(ReportMinuteDataService reportMinuteDataService) {
        this.reportMinuteDataService = reportMinuteDataService;
    }

    @Autowired
    public void setServerTrafficService(ServerTrafficService serverTrafficService) {
        this.serverTrafficService = serverTrafficService;
    }

    @Autowired
    public void setHistoricalUserLocationService(HistoricalUserLocationService historicalUserLocationService) {
        this.historicalUserLocationService = historicalUserLocationService;
    }

    @Autowired
    public void setActiveUserLocationService(ActiveUserLocationService activeUserLocationService) {
        this.activeUserLocationService = activeUserLocationService;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StatisticsVo getStatisticsData(StatisticsQueryDto dto) {
        List<Integer> clusterIds = new ArrayList<>();
        if (Objects.equals(dto.getClusterId(), 0)) {
            User user = userService.getById(dto.getUserId());
            List<Integer> ids;
            if (user.getUserType() == UserType.ADMIN) {
                ids = clusterService.getIds();
            } else {
                ids = clusterService.getByUserId(dto.getUserId());
                if (ids.isEmpty()) {
                    return null;
                }
            }
            clusterIds.addAll(ids);
        } else {
            clusterIds.add(dto.getClusterId());
        }
        val result = new StatisticsVo();
        //当天告警类型饼图
        final List<WarnTypeCount> warnTypeCountList = warnInfoService.getTypeCount(dto.getDate(), clusterIds);
        result.setWarnTypeCountList(warnTypeCountList);
        //告警增长率
        List<WarnIncreaseRate> warnIncreaseRates = warnInfoService.getWarnIncreaseRate(clusterIds);
        transform(warnIncreaseRates);
        result.setWarnIncreaseRates(warnIncreaseRates);
        //流量统计图,统计的是从0点到现在的总和
        val flowVos = new ArrayList<FlowVo>();
        //c2p流量统计
        val c2pFlowAdder = new LongAdder();
        //p2s流量统计
        val p2sFlowAdder = new LongAdder();
        //s2p流量统计
        val s2pFlowAdder = new LongAdder();
        //p2c流量统计
        val p2cFlowAdder = new LongAdder();
        //转发流量统计
        val relayFlowAdder = new LongAdder();
        List<StatisticsServerTrafficVo> statisticsData = serverTrafficService.getStatisticsData(dto.getDate(), clusterIds);
        statisticsData.forEach(data -> {
            val flowVo = new FlowVo();
            flowVo.setCreateTime(data.getCreateTime());
            flowVo.setC2pFlow(data.getPlatformC2pMessageReadFailFlow() + data.getPlatformC2pMessageReadSuccessFlow());
            c2pFlowAdder.add(flowVo.getC2pFlow());
            flowVo.setP2sFlow(data.getPlatformC2pMessageReadSuccessFlow());
            p2sFlowAdder.add(flowVo.getP2sFlow());
            flowVo.setS2pFlow(data.getPlatformS2pMessageReadSuccessFlow() + data.getPlatformS2pMessageReadFailFlow());
            s2pFlowAdder.add(flowVo.getS2pFlow());
//            flowVo.setP2cFlow(data.getPlatformS2pMessageReadSuccessFlow() - data.getPlatformP2pMessageRelayFlow());
            flowVo.setP2cFlow(data.getPlatformS2pMessageReadSuccessFlow());
            p2cFlowAdder.add(flowVo.getP2cFlow());
            flowVo.setRelayFlow(data.getPlatformP2pMessageRelayFlow());
            relayFlowAdder.add(flowVo.getRelayFlow());
            flowVos.add(flowVo);
        });
        result.setFlowVos(flowVos);
        final long toMb = 100 * 1024 * 1024L;
        result.setC2pFlow(MathUtil.reserveTwoDigits(c2pFlowAdder.longValue(), toMb));
        result.setP2sFlow(MathUtil.reserveTwoDigits(p2sFlowAdder.longValue(), toMb));
        result.setS2pFlow(MathUtil.reserveTwoDigits(s2pFlowAdder.longValue(), toMb));
        result.setP2cFlow(MathUtil.reserveTwoDigits(p2cFlowAdder.longValue(), toMb));
        result.setRelayFlow(MathUtil.reserveTwoDigits(relayFlowAdder.longValue(), toMb));
        //tps,qps折线图
        result.setTpsQps(reportMinuteDataService.getByClusterIds(dto.getDate(), clusterIds));
        //消息投递情况查询,统计的是当天的数据
        result.setMessageQuality(serverTrafficService.getMessageQualityByClusterIds(dto.getDate(), clusterIds));
        //人数分布
        List<UserLocationVo> location0 = historicalUserLocationService.getData();
        List<UserLocationVo> location1 = activeUserLocationService.getData();
        result.setHistoricalUserLocation(location0);
        result.setActiveUserLocation(location1);
        return result;
    }

    private void transform(List<WarnIncreaseRate> warnIncreaseRates) {
        if (warnIncreaseRates.isEmpty()) {
            return;
        }
        warnIncreaseRates.get(0).setIncreaseRate(new BigDecimal(100));
        int index = 1;
        int end = warnIncreaseRates.size() - 1;
        while (index <= end) {
            val warnCountCurrent = warnIncreaseRates.get(index);
            val warnCountBefore = warnIncreaseRates.get(index - 1);
            val increase = warnCountCurrent.getTotal() - warnCountBefore.getTotal();
            warnCountCurrent.setIncreaseRate(MathUtil.reserveTwoDigits(increase, warnCountBefore.getTotal()));
            index += 1;
        }
    }
}
