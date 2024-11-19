package com.netty100.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.netty100.config.constant.GlobalConstant;
import com.netty100.entity.ReportMinuteData;
import com.netty100.entity.Server;
import com.netty100.entity.WarnConfig;
import com.netty100.enumeration.ActionType;
import com.netty100.enumeration.WarnType;
import com.netty100.mapper.ClientConnectionHistoryMapper;
import com.netty100.mapper.ReportMinuteDataMapper;
import com.netty100.pojo.dto.ClientHistoryConnectDto;
import com.netty100.pojo.dto.ReportMinuteDataDto;
import com.netty100.pojo.dto.ServerWarnContentDto;
import com.netty100.pojo.vo.ForwardStatus;
import com.netty100.pojo.vo.MonitorTps;
import com.netty100.pojo.vo.MonitorTpsQpsPeak;
import com.netty100.pojo.vo.TpsQpsMinuteTotalVo;
import com.netty100.service.CommonWarnSendService;
import com.netty100.service.ReportMinuteDataService;
import com.netty100.service.ServerService;
import com.netty100.service.WarnConfigService;
import com.netty100.timer.config.TaskConfig;
import com.netty100.utils.DateUtils;
import com.netty100.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * @author why
 */
@Service
@Slf4j
public class ReportMinuteDataServiceImpl implements ReportMinuteDataService {

    private ReportMinuteDataMapper reportMinuteDataMapper;
    @Autowired
    private ServerService serverService;
    @Autowired
    private WarnConfigService warnConfigService;
    @Autowired
    private CommonWarnSendService commonWarnSendService;

    @Autowired
    public void setReportMinuteDataMapper(ReportMinuteDataMapper reportMinuteDataMapper) {
        this.reportMinuteDataMapper = reportMinuteDataMapper;
    }

    @Autowired
    private ClientConnectionHistoryMapper connectionHistoryMapper;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    @Transactional(readOnly = true)
    public ReportMinuteData getOne(String createDate, String createTime, Integer serverId) {
        return reportMinuteDataMapper.getOne(createDate, createTime, serverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(ReportMinuteData entity) {
        return reportMinuteDataMapper.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateById(ReportMinuteData entity) {
        return reportMinuteDataMapper.updateById(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportMinuteData getLatest(Integer serverId) {
        return reportMinuteDataMapper.getLatest(serverId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TpsQpsMinuteTotalVo> getByClusterIds(String date, List<Integer> clusterIds) {
        return reportMinuteDataMapper.getByClusterIds(date, clusterIds);
    }

    @Override
    public void serverReconnectCheck() {
        int readUnitCount = Math.max(0, TaskConfig.DEFAULT_READ_RECONNECT_UNIT_COUNT);
        String date = DateUtils.getFormatNow(DateUtils.YMD_DASH);
        List<String> times = DateUtils.getHmMBeforeTimes(readUnitCount + 1);
        List<Server> servers = serverService.findOpenMonitorServers();
        if (CollectionUtil.isEmpty(servers)) {
            return;
        }
        Map<ServerReconnectMonitorEnum, List<ServerWarnContentDto>> warnMap = new HashMap<>();
        servers.forEach(server -> {
            Integer serverId = server.getId();
            List<WarnConfig> warnConfigs = warnConfigService.getWarnConfig(serverId);
            if (CollectionUtil.isEmpty(warnConfigs)) {
                return;
            }
            ReportMinuteDataDto data = reportMinuteDataMapper.getAvgStatisticsByCreateTime(serverId, date, times);
            if (Objects.isNull(data)) {
                return;
            }
            for (ServerReconnectMonitorEnum serverReconnectMonitorEnum : ServerReconnectMonitorEnum.values()) {
                long reconnectTimes = serverReconnectMonitorEnum.fieldValueFun.applyAsLong(data);
                BigDecimal acgTimes = MathUtil.toBigDecimal(reconnectTimes);
                List<WarnConfig> configList = serverReconnectMonitorEnum.configValueFun.apply(warnConfigs, serverReconnectMonitorEnum.warnType.getIndex());
                BigDecimal limitThreshold = CollectionUtil.isNotEmpty(configList) ? configList.get(0).getTypeThreshold() : BigDecimal.ZERO;
                if (limitThreshold.compareTo(BigDecimal.ZERO) != 0
                        && MathUtil.compare(limitThreshold, acgTimes)) {
                    warnMap.compute(serverReconnectMonitorEnum, (k, v) -> {
                        if (Objects.isNull(v)) {
                            v = new ArrayList<>();
                        }
                        ServerWarnContentDto warnContentDto = this.commonWarnSendService.warpWarn(server, limitThreshold,
                                acgTimes, serverReconnectMonitorEnum.warnType);
                        v.add(warnContentDto);
                        return v;
                    });
                }
            }
        });
        warnMap.forEach((k, v) -> {
            if (CollectionUtil.isNotEmpty(v)) {
                commonWarnSendService.warnServerSend(v, k.warnType, times);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ForwardStatus getForwardStatus(Integer clusterId, Integer serverId, String date, String time) {
        return reportMinuteDataMapper.getForwardStatus(clusterId, serverId, date, time);
    }


    public enum ServerReconnectMonitorEnum {
        /// 游戏端平均连接数
        CLIENT_CONNECTION(ReportMinuteDataDto::getAvgErrorReconnectTime, WarnType.CLIENT_ERROR_RECONNECT_TIMES),
        // 游戏端异常断开数量
        CLIENT_CONNECTION_ERROR(ReportMinuteDataDto::getAvgIdleReconnectTime, WarnType.CLIENT_IDLE_RECONNECT_TIMES);

        private ToLongFunction<ReportMinuteDataDto> fieldValueFun;
        private BiFunction<List<WarnConfig>, Integer, List<WarnConfig>> configValueFun;
        private WarnType warnType;

        ServerReconnectMonitorEnum(ToLongFunction<ReportMinuteDataDto> fieldValueFun, WarnType warnType) {
            this.fieldValueFun = fieldValueFun;
            this.configValueFun = (warnConfigs, index) ->
                    warnConfigs.stream().filter(warnConfig -> Objects.equals(warnConfig.getTypeIndex(), index)).collect(Collectors.toList());
            this.warnType = warnType;
        }
    }

    @Override
    public void serverReconnectCalc(String time, String sixMinuteAgoTime, String oneMinuteAgoTime) {
        // 错误信息容器
//        ConcurrentMap<String, AtomicInteger> errorCountMap = new ConcurrentHashMap<>();
//        ConcurrentMap<String, AtomicInteger> idleCountMap = new ConcurrentHashMap<>();
        ConcurrentMap<String, AtomicIntegerArray> countArrayMap = new ConcurrentHashMap<>();
        // 默认表名
        String defaultTableName = GlobalConstant.DEFAULT_LOG_TABLE_NAME_PREFIX;
        // 断开状态列表
        List<Integer> disConnectStatus = Arrays.asList(
                ActionType.CLIENT_DISCONNECT,
                ActionType.CLIENT_ERROR_DISCONNECT,
                ActionType.CLIENT_HEARTBEAT_DISCONNECT);
        List<Integer> exConnectStatus = Arrays.asList(
                ActionType.CLIENT_ERROR_DISCONNECT,
                ActionType.CLIENT_HEARTBEAT_DISCONNECT);
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        IntStream.range(0, GlobalConstant.DEFAULT_LOG_TABLE_COUNT)
                .forEach(index -> {
                    CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            String tableName = defaultTableName.concat(String.valueOf(index));
                            // 将所有 按照上报时间倒序排序
                            List<ClientHistoryConnectDto> historyConnects = connectionHistoryMapper.getRangeTimeHistoryConnect(
                                    tableName,
                                    sixMinuteAgoTime.concat(GlobalConstant.CONCAT_STR),
                                    time.concat(GlobalConstant.CONCAT_STR));
                            Map<Long, List<ClientHistoryConnectDto>> userHistoryConnects = historyConnects
                                    .parallelStream()
                                    .collect(Collectors
                                            .groupingBy(ClientHistoryConnectDto::getUserId));
                            userHistoryConnects.forEach((k, v) -> {
                                if (CollectionUtil.isEmpty(v)) {
                                    return;
                                }
                                // 获取到一分钟内的所有数据
                                List<ClientHistoryConnectDto> oneMinuteData = v
                                        .stream()
                                        .filter(a -> a.getOccurTime()
                                                .compareTo(oneMinuteAgoTime
                                                        .concat(GlobalConstant.CONCAT_STR)) >= 0)
                                        .collect(Collectors.toList());
                                if (CollectionUtil.isEmpty(oneMinuteData)) {
                                    return;
                                }
                                ClientHistoryConnectDto dto = oneMinuteData.get(0);
                                Integer actionType = dto.getActionType();
                                if (disConnectStatus.contains(actionType)) {
                                    oneMinuteData.remove(0);
                                }
                                int size = oneMinuteData.size();
                                if (CollectionUtil.isEmpty(oneMinuteData)) {
                                    return;
                                }
                                if (Objects.equals(1, size)) {
                                    String occurTime = oneMinuteData.get(0).getOccurTime();
                                    String limitMinute = DateUtils.stringDatePlusMinute(occurTime, -5);
                                    Optional<ClientHistoryConnectDto> optional = v.stream()
                                            .filter(x -> x.getOccurTime().compareTo(limitMinute) >= 0
                                                    && x.getOccurTime().compareTo(occurTime) < 0)
                                            .findFirst();
                                    if (optional.isPresent()) {
                                        ClientHistoryConnectDto connectDto = optional.get();
                                        if (exConnectStatus.contains(connectDto.getActionType())) {
                                            doFillMap(connectDto, connectDto.getClusterId(), oneMinuteData.get(0).getServerId(), countArrayMap);
                                        }
                                    }
                                    return;
                                }
                                for (int i = 1; i < size; i++) {
                                    ClientHistoryConnectDto connectDto = oneMinuteData.get(i);
                                    doFillMap(connectDto, connectDto.getClusterId(), oneMinuteData.get(i - 1).getServerId(), countArrayMap);
                                }
                                ClientHistoryConnectDto oneMinuteLastDto = oneMinuteData.get(size - 1);
                                Integer lastOneActionType = oneMinuteLastDto.getActionType();
                                if (Objects.equals(ActionType.CLIENT_CONNECT, lastOneActionType)) {
                                    String occurTime = oneMinuteLastDto.getOccurTime();
                                    String limitMinute = DateUtils.stringDatePlusMinute(occurTime, -5);
                                    // 如果是连接状态  则通过该历史记录向前推5分钟 并且小于该occur time的 取出第一条即可 链路 肯定是 连->断-连-断
                                    // 如果出现连连断断 不处理
                                    Optional<ClientHistoryConnectDto> optional = v.stream()
                                            .filter(x -> x.getOccurTime().compareTo(limitMinute) >= 0
                                                    && x.getOccurTime().compareTo(occurTime) < 0)
                                            .findFirst();
                                    if (optional.isPresent()) {
                                        ClientHistoryConnectDto connectDto = optional.get();
                                        if (exConnectStatus.contains(connectDto.getActionType())) {
                                            doFillMap(connectDto, connectDto.getClusterId(), oneMinuteLastDto.getServerId(), countArrayMap);
                                        }
                                    }
                                }
                            });
                        } catch (Exception e) {
                            log.error("统计信息异常", e);
                        }
                        return index;
                    }, taskExecutor);
                    future.whenCompleteAsync((x, y) -> {
                        if (Objects.nonNull(y)) {
                            log.error(x + "执行异常", y);
                        } else {
                            log.info("future:{}执行成功", x);
                        }
                    });
                    taskList.add(future);
                });
        CompletableFuture<Void> allFuture = CompletableFuture.allOf(taskList.toArray(new CompletableFuture[taskList.size()]));
        allFuture.join();
        taskExecutor.execute(() -> {
            operate2db(oneMinuteAgoTime, countArrayMap);
        });
    }

    /**
     * 将统计的信息入库,report_minute_data
     *
     * @param oneMinuteAgoTime 计算的时间对应 create_time和create_date
     * @param countArrayMap    异常/心跳断开连接容器信息
     */
    private void operate2db(String oneMinuteAgoTime, ConcurrentMap<String, AtomicIntegerArray> countArrayMap) {
        String[] dateArr = oneMinuteAgoTime.split(GlobalConstant.SPLIT_STR);
        countArrayMap.forEach((k, v) -> {
            String[] clusterAndServer = k.split(GlobalConstant.SPLIT_CLUSTER_SERVER);
            ReportMinuteData minuteErrorData = new ReportMinuteData();
            minuteErrorData.setServerId(Integer.parseInt(clusterAndServer[1]));
            minuteErrorData.setClusterId(Integer.parseInt(clusterAndServer[0]));
            minuteErrorData.setClientErrorReconnectTimes(v.get(0));
            minuteErrorData.setClientIdleReconnectTimes(v.get(1));
            minuteErrorData.setCreateDate(dateArr[0]);
            minuteErrorData.setCreateTime(dateArr[1]);
            saveClientReconnectTimes(minuteErrorData);
        });
    }

    private void saveClientReconnectTimes(ReportMinuteData minuteErrorData) {
        reportMinuteDataMapper.saveClientReconnectTimes(minuteErrorData);
    }

    @Override
    public MonitorTps getMonitorTps(Integer clusterId, Integer serverId, String date, String time) {
        return reportMinuteDataMapper.getMonitorTps(clusterId, serverId, date, time);
    }

    @Override
    public MonitorTpsQpsPeak getMonitorTpsQpsPeak(Integer clusterId, Integer serverId, String date) {
        return reportMinuteDataMapper.getMonitorTpsQpsPeak(clusterId, serverId, date);
    }

    @Override
    public void saveConnectionCount(ReportMinuteData entity) {
        reportMinuteDataMapper.saveConnectionCount(entity);
    }

    @Override
    public void saveTrafficFlowStatus(ReportMinuteData data) {
        reportMinuteDataMapper.saveTrafficFlowStatus(data);
    }

    private void doFillMap(ClientHistoryConnectDto connectDto, Integer clusterId, Integer serverId, ConcurrentMap<String, AtomicIntegerArray> atomicIntegerArrayConcurrentMap) {
        Integer innerActionType = connectDto.getActionType();
        if (Objects.equals(innerActionType, ActionType.CLIENT_ERROR_DISCONNECT)) {
            // 异常断开
            AtomicIntegerArray atomicIntegerArray = atomicIntegerArrayConcurrentMap
                    .putIfAbsent(clusterId + "/" + serverId, new AtomicIntegerArray(new int[]{1, 0}));
            if (Objects.nonNull(atomicIntegerArray)) {
                atomicIntegerArray.getAndIncrement(0);
            }
        }
        if (Objects.equals(innerActionType, ActionType.CLIENT_HEARTBEAT_DISCONNECT)) {
            // 心跳断开
            AtomicIntegerArray atomicIntegerArray = atomicIntegerArrayConcurrentMap
                    .putIfAbsent(clusterId + "/" + serverId, new AtomicIntegerArray(new int[]{0, 1}));
            if (Objects.nonNull(atomicIntegerArray)) {
                atomicIntegerArray.getAndIncrement(1);
            }
        }
    }
}
