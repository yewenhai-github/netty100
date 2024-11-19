package com.netty100.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.netty100.entity.Cluster;
import com.netty100.entity.ReportWarnStatistics;
import com.netty100.entity.ReportWarnStatisticsRate;
import com.netty100.entity.WarnHistoryInfo;
import com.netty100.enumeration.WarnType;
import com.netty100.mapper.ReportWarnStatisticsMapper;
import com.netty100.mapper.WarnHistoryInfoMapper;
import com.netty100.pojo.dto.WarnHistoryRateStatisticsDto;
import com.netty100.pojo.dto.WarnStatisticsDto;
import com.netty100.pojo.vo.WarnIncreaseRate;
import com.netty100.pojo.vo.WarnIndexCount;
import com.netty100.pojo.vo.WarnTypeCount;
import com.netty100.service.ClusterService;
import com.netty100.service.ReportWarnStatisticsRateService;
import com.netty100.service.ServerService;
import com.netty100.service.WarnInfoService;
import com.netty100.utils.DateUtils;
import com.netty100.utils.MathUtil;
import com.netty100.utils.exception.CommonException;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Service
public class WarnInfoServiceImpl implements WarnInfoService {
    @Autowired
    private WarnHistoryInfoMapper warnInfoMapper;
    @Autowired
    private ReportWarnStatisticsMapper warnStatisticsMapper;

    @Autowired
    private ServerService serverService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ReportWarnStatisticsRateService reportWarnStatisticsRateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(WarnHistoryInfo warnInfo) {
        return warnInfoMapper.save(warnInfo);
    }

    @Override
    public void clusterWarnStatistics() {
        String lastDay = DateUtils.formatYMDPlusDay(-1, DateUtils.YMD_DASH);
        String theDayBeforeYesterday = DateUtils.formatYMDPlusDay(-2, DateUtils.YMD_DASH);
        // 获取前一天的告警信息
        List<WarnStatisticsDto> statistics = warnInfoMapper.getWarnInfoStatistics(lastDay);
        // 获取到所有的集群id
        List<Cluster> clusters = clusterService.queryAll();
        List<Integer> clusterIds = clusters
                .stream()
                .map(Cluster::getId)
                .collect(Collectors.toList());
        List<ReportWarnStatistics> warnStatistics = statistics
                .stream()
                .map(statistic -> {
                    ReportWarnStatistics warnStatistic = new ReportWarnStatistics();
                    BeanUtil.copyProperties(statistic, warnStatistic, true);
                    warnStatistic.setStatisticsDay(lastDay);
                    return warnStatistic;
                }).collect(Collectors.toList());
        // 入库
        CompletableFuture.runAsync(() -> {
            if (CollectionUtil.isNotEmpty(warnStatistics)) {
                warnStatisticsMapper.insertBatch(warnStatistics);
                warnInfoMapper.updateByCreateDay(lastDay);
            }
            List<WarnHistoryRateStatisticsDto> warnHistoryRateStatisticsDtos = warnStatisticsMapper
                    .selectRateStatistics(Arrays.asList(theDayBeforeYesterday, lastDay), clusterIds);
            Map<String, List<WarnHistoryRateStatisticsDto>> map = warnHistoryRateStatisticsDtos
                    .stream()
                    .collect(Collectors.groupingBy(WarnHistoryRateStatisticsDto::getCreateDay));
            List<WarnHistoryRateStatisticsDto> denominatorStatistics = map.get(theDayBeforeYesterday);

            List<WarnHistoryRateStatisticsDto> numeratorStatistics = map.get(lastDay);
            List<ReportWarnStatisticsRate> rateList = clusterIds.parallelStream().map(c -> {
                Integer theDayBeforeYesterdayCount = getCount(denominatorStatistics, c);
                Integer yesterdayCount = getCount(numeratorStatistics, c);
                BigDecimal rate = getIncreaseRate(theDayBeforeYesterdayCount, yesterdayCount);
                ReportWarnStatisticsRate reportWarnStatisticsRate = new ReportWarnStatisticsRate();
                reportWarnStatisticsRate.setCalcDay(lastDay);
                reportWarnStatisticsRate.setClusterId(c);
                reportWarnStatisticsRate.setIncreaseRate(rate);
                return reportWarnStatisticsRate;
            }).collect(Collectors.toList());
            reportWarnStatisticsRateService.insertBatch(rateList);
        });
    }

    private BigDecimal getIncreaseRate(Integer theDayBeforeYesterdayCount, Integer yesterdayCount) {
        BigDecimal rate = BigDecimal.ZERO;
        boolean denominatorZero = Objects.equals(0, theDayBeforeYesterdayCount);
        boolean numeratorZero = Objects.equals(0, yesterdayCount);
        if (denominatorZero && numeratorZero) {
            return rate;
        }
        if (denominatorZero && !numeratorZero) {
            return BigDecimal.valueOf(100);
        }
        int increase = yesterdayCount - theDayBeforeYesterdayCount;
        rate = BigDecimal.valueOf(increase).divide(MathUtil.toBigDecimal(theDayBeforeYesterdayCount), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return rate;
    }

    private Integer getCount(List<WarnHistoryRateStatisticsDto> numeratorStatistics, Integer c) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        if (CollectionUtil.isNotEmpty(numeratorStatistics)) {
            numeratorStatistics.parallelStream().forEach(warnHistoryRateStatisticsDto -> {
                Integer clusterId = warnHistoryRateStatisticsDto.getClusterId();
                if (Objects.equals(c, clusterId)) {
                    // 如果集群id对应上 获取前天的数据
                    count.set(warnHistoryRateStatisticsDto.getCount());
                    return;
                }
            });
        }
        return count.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarnTypeCount> getTypeCount(String date, List<Integer> clusterIds) {
        List<WarnIndexCount> typeIndexCount = warnInfoMapper.getTypeCount(date, clusterIds);
        final int total = typeIndexCount.stream().map(WarnIndexCount::getTimes).reduce(0, Integer::sum);
        return typeIndexCount.stream().map(x -> {
            val typeCount = new WarnTypeCount();
            typeCount.setWarnTypeName(getWarnTypeName(x.getWarnTypeIndex()));
            typeCount.setTimes(x.getTimes());
            typeCount.setRate(MathUtil.reserveTwoDigits(x.getTimes(), total));
            return typeCount;
        }).collect(Collectors.toList());
    }

    @Override
    public List<WarnHistoryInfo> getWarnInfoList(Integer clusterId, Integer serverId, String date) {
        return warnInfoMapper.getWarnInfoList(clusterId, serverId, date);
    }

    @Override
    public List<WarnIncreaseRate> getWarnIncreaseRate(List<Integer> clusterIds) {
        return warnStatisticsMapper.getWarnIncreaseRate(clusterIds);
    }

    private String getWarnTypeName(int index) {
        final WarnType[] values = WarnType.values();
        final Optional<WarnType> optional = Arrays.stream(values).filter(x -> x.getIndex() == index).findFirst();
        if (optional.isPresent()) {
            return optional.get().getShortName();
        } else {
            throw new CommonException(String.format("错误的告警类型索引:%d", index));
        }
    }
}
