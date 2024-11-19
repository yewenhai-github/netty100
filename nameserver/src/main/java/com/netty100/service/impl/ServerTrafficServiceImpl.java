package com.netty100.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.netty100.entity.Server;
import com.netty100.entity.ServerTraffic;
import com.netty100.entity.WarnConfig;
import com.netty100.enumeration.WarnType;
import com.netty100.mapper.ServerTrafficMapper;
import com.netty100.pojo.dto.ServerTrafficDto;
import com.netty100.pojo.dto.ServerWarnContentDto;
import com.netty100.pojo.vo.*;
import com.netty100.service.CommonWarnSendService;
import com.netty100.service.ServerService;
import com.netty100.service.ServerTrafficService;
import com.netty100.service.WarnConfigService;
import com.netty100.timer.config.TaskConfig;
import com.netty100.utils.DateUtils;
import com.netty100.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.misc.FloatingDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Slf4j
@Service
public class ServerTrafficServiceImpl implements ServerTrafficService {

    @Value("${servers.message.clearDays}")
    private int clearDays;

    @Value("${servers.message.readCount}")
    private int readCount;

    @Value("${servers.message.limitRadio}")
    private BigDecimal limitRadio;

    @Autowired
    private ServerTrafficMapper serverTrafficMapper;

    @Autowired
    private ServerService serverService;

    @Autowired
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    private WarnConfigService warnConfigService;

    @Autowired
    private CommonWarnSendService commonWarnSendService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(ServerTraffic serverTraffic) {
        return serverTrafficMapper.save(serverTraffic);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsServerTrafficVo> getStatisticsData(String date, List<Integer> clusterIds) {
        return serverTrafficMapper.getStatisticsData(date, clusterIds);
    }

    @Override
    public void serverMessageRemove() {
        int finalClearDays = Math.min(clearDays, TaskConfig.SERVER_MESSAGE_REMOVE_DAYS);
        String removeDay = DateUtils.formatYMDPlusDay(finalClearDays, DateUtils.YMD_DASH);
        if (StringUtils.isEmpty(removeDay)) {
            return;
        }
        // 获取小于该时间的记录
        List<Integer> ids;
        do {
            // 每次读取清理日期的1000（默认）条数据
            ids = serverTrafficMapper.queryRemoveRecordIds(removeDay,
                    TaskConfig.SERVER_MESSAGE_REMOVE_COUNT);
            if (CollectionUtil.isNotEmpty(ids)) {
                serverTrafficMapper.clearMessage(ids);
            }
        } while (CollectionUtil.isNotEmpty(ids));
    }

    @Override
    public void serverMessageThresholdMonitor() {
        List<Server> servers = serverService.findOpenMonitorServers();
        if (CollectionUtil.isEmpty(servers)) {
            return;
        }
        Map<ServerMonitorEnum, List<ServerWarnContentDto>> warnMap = new HashMap<>();
        int readUnitCount = Math.max(readCount, TaskConfig.DEFAULT_READ_UNIT_COUNT);
        List<String> times = DateUtils.getHmMBeforeTimes(readUnitCount + 1);
        servers.forEach(server -> {
            Integer serverId = server.getId();
            // 如果未配置告警信息 直接阻断
            List<WarnConfig> warnConfigs = warnConfigService.getWarnConfig(serverId);
            if (CollectionUtil.isEmpty(warnConfigs)) {
                return;
            }
            // 获取各种统计信息
            String date = DateUtils.getFormatNow(DateUtils.YMD_DASH);

            List<ServerTrafficDto> serverMessages = serverTrafficMapper.queryServerMessageByCreateTime(serverId, date, times);
            if (CollectionUtil.isEmpty(serverMessages)) {
                return;
            }
            for (ServerMonitorEnum serverMonitor : ServerMonitorEnum.values()) {
                BigDecimal averageStatistics = getAvgStatistics(serverMessages, serverMonitor);
                // 阈值为零不告警
                BigDecimal warnThreshold = getWarnThreshold(warnConfigs, serverMonitor);
                if (warnThreshold.compareTo(BigDecimal.ZERO) != 0 && MathUtil.compare(warnThreshold, averageStatistics)) {
                    warnMap.compute(serverMonitor, (k, v) -> {
                        if (Objects.isNull(v)) {
                            v = new ArrayList<>();
                        }
                        ServerWarnContentDto warnContentDto = commonWarnSendService.warpWarn(server, warnThreshold, averageStatistics, serverMonitor.warnType);
                        v.add(warnContentDto);
                        return v;
                    });
                }
            }
            setNever2check(serverMessages, warnConfigs);
        });
        warnMap.forEach((k, v) -> {
            check2warn(v, k.warnType, times);
        });
    }

    private void setNever2check(List<ServerTrafficDto> serverMessages, List<WarnConfig> warnConfigs) {
        serverMessages.forEach(serverTrafficDto -> {
            Long totalCount = serverTrafficDto.getTotalCount();
            Long totalTraffic = serverTrafficDto.getTotalTraffic();
            Integer id = serverTrafficDto.getId();
            if (MathUtil.compare(getWarnThreshold(warnConfigs, ServerMonitorEnum.TRAFFIC_SERVER_BYTE_SIZE), MathUtil.toBigDecimal(totalTraffic))
                    || MathUtil.compare(getWarnThreshold(warnConfigs, ServerMonitorEnum.TRAFFIC_SERVER_COUNT), MathUtil.toBigDecimal(totalCount))) {
                updateRecordNotStatistics(id);
            }
        });
    }

    /**
     * 两个率的统计需要重新梳理,单独处理
     *
     * @param serverMessages
     * @param serverMonitor
     * @return
     */
    private BigDecimal getAvgStatistics(List<ServerTrafficDto> serverMessages, ServerMonitorEnum serverMonitor) {
        BigDecimal averageStatistics;
        if (serverMonitor.equals(ServerMonitorEnum.FAILED_MESSAGE_RATE)) {
            averageStatistics = getFailedMessageRate(serverMessages);
        } else if (serverMonitor.equals(ServerMonitorEnum.FORWARD_MESSAGE_RATE)) {
            averageStatistics = new BigDecimal("-0.1");
        } else {
            averageStatistics = getAverageStatistics(serverMessages, serverMonitor.fieldValueFun);
        }
        return averageStatistics;
    }

    /**
     * 流量阈值需要计算下
     *
     * @param warnConfigs
     * @param serverMonitor
     * @return
     */
    private BigDecimal getWarnThreshold(List<WarnConfig> warnConfigs, ServerMonitorEnum serverMonitor) {
        log.info("serverMonitor##########{}", serverMonitor.warnType.getTitle());
        List<WarnConfig> configList = serverMonitor.configValueFun.apply(warnConfigs, serverMonitor.warnType.getIndex());
        BigDecimal limitThreshold = CollectionUtil.isNotEmpty(configList) ? configList.get(0).getTypeThreshold() : BigDecimal.ZERO;
        if (serverMonitor.equals(ServerMonitorEnum.TRAFFIC_SERVER_BYTE_SIZE)
                || serverMonitor.equals(ServerMonitorEnum.TRAFFIC_SERVER_COUNT)) {
            limitThreshold = limitRadio.multiply(limitThreshold);
        }
        return limitThreshold;
    }

    @Override
    public void updateRecordNotStatistics(Integer id) {
        serverTrafficMapper.updateRecordNotStatistics(id);
    }

    @Override
    public MessageQuality getMessageQualityByClusterIds(String date, List<Integer> clusterIds) {
        return serverTrafficMapper.getMessageQualityByClusterIds(date, clusterIds);
    }

    @Override
    public TrafficConnectionFlowVo getMonitorData(Integer clusterId, Integer serverId, String date, String time) {
        return serverTrafficMapper.getMonitorData(clusterId, serverId, date, time);
    }

    @Override
    public TrafficTotalVo getTotal(Integer serverId, String createDate) {
        return serverTrafficMapper.getTotal(serverId, createDate);
    }

    @Override
    public MessageRateVo getMessageRate(Integer clusterId, Integer serverId, String date, String time) {
        return serverTrafficMapper.getMessageRate(clusterId, serverId, date, time);
    }

    @Override
    public List<ServerTrafficConnectionVo> getConnectionData(String date, Integer clusterId, Integer serverId) {
        return serverTrafficMapper.getConnectionData(date, clusterId, serverId);
    }

    @Override
    public BigInteger getDayTotalTraffic(Integer clusterId, Integer serverId, String date) {
        return serverTrafficMapper.getDayTotalTraffic(clusterId, serverId, date);
    }

    @Override
    public ForwardRateDetectionVo getForwardRateVo(Integer clusterId, String createDate, String createTime) {
        return serverTrafficMapper.getForwardRateVo(clusterId, createDate, createTime);
    }

    private void check2warn(List<ServerWarnContentDto> servers, WarnType warnType, List<String> times) {
        if (CollectionUtil.isNotEmpty(servers)) {
            commonWarnSendService.warnServerSend(servers, warnType, times);
        }
    }

    private BigDecimal getAverageStatistics(List<ServerTrafficDto> serverMessages, ToLongFunction<ServerTrafficDto> function) {
        OptionalDouble optionalDouble = serverMessages.stream().mapToLong(function).average();
        BigDecimal result = BigDecimal.ZERO;
        if (optionalDouble.isPresent()) {
            result = MathUtil.toBigDecimal(FloatingDecimal.toJavaFormatString(optionalDouble.getAsDouble()));
            result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return result;
    }

    private BigDecimal getFailedMessageRate(List<ServerTrafficDto> serverMessages) {
        // 总
        long sumPlatformC2pMessageReadSuccessTotal = getSumStatistics(serverMessages,
                ServerTrafficDto::getPlatformC2pMessageReadSuccessTotal);
        long sumPlatformC2pMessageReadFailTotal = getSumStatistics(serverMessages,
                ServerTrafficDto::getPlatformC2pMessageReadFailTotal);
        long sumPlatformS2pMessageReadSuccessTotal = getSumStatistics(serverMessages,
                ServerTrafficDto::getPlatformS2pMessageReadSuccessTotal);
        long sumPlatformS2pMessageReadFailTotal = getSumStatistics(serverMessages,
                ServerTrafficDto::getPlatformS2pMessageReadFailTotal);
        long allTotal = sumPlatformC2pMessageReadSuccessTotal + sumPlatformC2pMessageReadFailTotal
                + sumPlatformS2pMessageReadSuccessTotal + sumPlatformS2pMessageReadFailTotal;
        // 失败
        long failTotal = sumPlatformS2pMessageReadFailTotal + sumPlatformC2pMessageReadFailTotal;
        if (Objects.equals(allTotal, 0)) {
            return BigDecimal.ZERO;
        }
        return MathUtil.reserveTwoDigits(failTotal, allTotal);
    }

    private BigDecimal getForwardMessageRate(List<ServerTrafficDto> serverMessages) {
        Long sumPlatformC2pMessageReadSuccessTotal = getSumStatistics(serverMessages,
                ServerTrafficDto::getPlatformC2pMessageReadSuccessTotal);
        Long sumPlatformS2pMessageReadSuccessTotal = getSumStatistics(serverMessages,
                ServerTrafficDto::getPlatformS2pMessageReadSuccessTotal);
        Long sumPlatformP2pMessageRelayTotal = getSumStatistics(serverMessages,
                ServerTrafficDto::getPlatformP2pMessageRelayTotal);
        long allTotal = sumPlatformC2pMessageReadSuccessTotal + sumPlatformS2pMessageReadSuccessTotal;
        if (sumPlatformP2pMessageRelayTotal > allTotal) {
            sumPlatformP2pMessageRelayTotal = allTotal;
        }
        if (Objects.equals(allTotal, 0)) {
            return BigDecimal.ZERO;
        }
        return MathUtil.reserveTwoDigits(sumPlatformP2pMessageRelayTotal, allTotal);
    }

    private Long getSumStatistics(List<ServerTrafficDto> serverMessages, ToLongFunction<ServerTrafficDto> function) {
        return serverMessages.stream().mapToLong(function).sum();
    }

    public enum ServerMonitorEnum {
        /// 游戏端平均连接数
        CLIENT_CONNECTION(ServerTrafficDto::getPlatformC2pConnectActiveTotal, WarnType.CLIENT_CONNECTION_TOO_MANY),
        // 游戏端异常断开数量
        CLIENT_CONNECTION_ERROR(ServerTrafficDto::getPlatformC2pConnectErrorTotal, WarnType.CLIENT_ERROR_DISCONNECT_COUNT),
        // 服务器异常断开
        APPLICATION_CONNECTION_ERROR(ServerTrafficDto::getPlatformS2pConnectErrorTotal, WarnType.SERVER_ERROR_DISCONNECT_COUNT),
        // 游戏端投递失败
        CLIENT_READ_FAIL_COUNT(ServerTrafficDto::getPlatformC2pMessageReadFailTotal, WarnType.CLIENT_READ_FAILED_COUNT),
        // 游戏端投递失败平均流量
        CLIENT_READ_FAIL_FLOW(ServerTrafficDto::getPlatformC2pMessageReadFailFlow, WarnType.CLIENT_READ_FAILED_SIZE),
        // 服务器投递失败次数
        APPLICATION_READ_FAIL_COUNT(ServerTrafficDto::getPlatformS2pMessageReadFailTotal, WarnType.SERVER_READ_FAILED_COUNT),
        // 服务器投递失败流量
        APPLICATION_READ_FAIL_FLOW(ServerTrafficDto::getPlatformS2pMessageReadFailFlow, WarnType.SERVER_READ_FAILED_SIZE),
        // 消息转发次数
        FORWARD_COUNT(ServerTrafficDto::getPlatformP2pMessageRelayTotal, WarnType.KERNEL_FORWARD_COUNT),
        TRAFFIC_SERVER_BYTE_SIZE(ServerTrafficDto::getTotalTraffic, WarnType.KERNEL_TRAFFIC_SIZE),
        TRAFFIC_SERVER_COUNT(ServerTrafficDto::getTotalCount, WarnType.KERNEL_TRAFFIC_COUNT),
        // 重连次数
        FAILED_MESSAGE_RATE(null, WarnType.CLIENT_READ_FAILED_RATE),
        // 转发率
        FORWARD_MESSAGE_RATE(null, WarnType.KERNEL_FORWARD_RATE);
        private ToLongFunction<ServerTrafficDto> fieldValueFun;
        private BiFunction<List<WarnConfig>, Integer, List<WarnConfig>> configValueFun;
        private WarnType warnType;

        ServerMonitorEnum(ToLongFunction<ServerTrafficDto> fieldValueFun, WarnType warnType) {
            this.fieldValueFun = fieldValueFun;
            this.configValueFun = (warnConfigs, index) ->
                    warnConfigs.stream().filter(warnConfig -> Objects.equals(warnConfig.getTypeIndex(), index)).collect(Collectors.toList());
            this.warnType = warnType;
        }
    }
}
