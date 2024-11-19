package com.netty100.config.websocket;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Tuple;
import com.netty100.entity.User;
import com.netty100.entity.WarnHistoryInfo;
import com.netty100.enumeration.UserType;
import com.netty100.enumeration.WarnType;
import com.netty100.pojo.vo.*;
import com.netty100.service.*;
import com.netty100.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author why
 */
@SuppressWarnings("DuplicatedCode")
@Slf4j
@Component
public class TaskManager {

    /**
     * 定时任务的执行间隔,默认为30秒
     */
    private static final Integer DEFAULT_DELAY = 30000;

    /**
     * 定时任务第一次执行延迟事件,默认为3秒
     */
    private static final Integer FIRST_DELAY = 5000;

    private final ConcurrentHashMap<Integer, List<ScheduledFuture<?>>> taskMap = new ConcurrentHashMap<>();

    private TaskScheduler taskScheduler;

    private ReportMinuteDataService reportMinuteDataService;

    private ServerTrafficService serverTrafficService;

    private ClusterService clusterService;

    private HistoricalUserLocationService historicalUserLocationService;

    private ActiveUserLocationService activeUserLocationService;

    private UserService userService;

    private WarnInfoService warnInfoService;

    private MessageSender messageSender;

    private ServerService serverService;

    @Autowired
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

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

    public void removeTaskIfPresent(Integer userId) {
        taskMap.computeIfPresent(userId, (k, v) -> {
            v.forEach(scheduledFuture -> scheduledFuture.cancel(true));
            log.info("userId={}成功移除之前的定时任务", userId);
            return v;
        });
        taskMap.remove(userId);
    }

    private List<ScheduledFuture<?>> addStatisticsTask(List<Integer> clusterIds, Integer userId) {
        val list = new ArrayList<ScheduledFuture<?>>();
        val startTime = DateUtils.addMilliseconds(new Date(), FIRST_DELAY);
        //当天告警类型饼图
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            val warnTypeCountList = warnInfoService.getTypeCount(DateUtil.today(), clusterIds);
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-warnTypeCountList", warnTypeCountList));
        }, startTime, DEFAULT_DELAY));
        //tps-qps折线图
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            List<TpsQpsMinuteTotalVo> result = reportMinuteDataService.getByClusterIds(DateUtil.today(), clusterIds);
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-tpsQps", result));
        }, startTime, DEFAULT_DELAY));
        //流量占比统计(天),流量折线图
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            val flowVos = new ArrayList<FlowVo>();
            val c2pFlowAdder = new LongAdder();
            val p2sFlowAdder = new LongAdder();
            val s2pFlowAdder = new LongAdder();
            val p2cFlowAdder = new LongAdder();
            val relayFlowAdder = new LongAdder();
            List<StatisticsServerTrafficVo> statisticsData = serverTrafficService.getStatisticsData(DateUtil.today(), clusterIds);
            statisticsData.forEach(data -> {
                val flowVo = new FlowVo();
                flowVo.setCreateTime(data.getCreateTime());
                flowVo.setC2pFlow(data.getPlatformC2pMessageReadFailFlow() + data.getPlatformC2pMessageReadSuccessFlow());
                c2pFlowAdder.add(flowVo.getC2pFlow());
                flowVo.setP2sFlow(data.getPlatformC2pMessageReadSuccessFlow());
                p2sFlowAdder.add(flowVo.getP2sFlow());
                flowVo.setS2pFlow(data.getPlatformS2pMessageReadSuccessFlow() + data.getPlatformS2pMessageReadFailFlow());
                s2pFlowAdder.add(flowVo.getS2pFlow());
//                flowVo.setP2cFlow(data.getPlatformS2pMessageReadSuccessFlow() - data.getPlatformP2pMessageRelayFlow());
                flowVo.setP2cFlow(data.getPlatformS2pMessageReadSuccessFlow());
                p2cFlowAdder.add(flowVo.getP2cFlow());
                flowVo.setRelayFlow(data.getPlatformP2pMessageRelayFlow());
                relayFlowAdder.add(flowVo.getRelayFlow());
                flowVos.add(flowVo);
            });
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-flowVos", flowVos));
            final long toMb = 100 * 1024 * 1024L;
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-c2pFlow", MathUtil.reserveTwoDigits(c2pFlowAdder.longValue(), toMb)));
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-p2sFlow", MathUtil.reserveTwoDigits(p2sFlowAdder.longValue(), toMb)));
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-s2pFlow", MathUtil.reserveTwoDigits(s2pFlowAdder.longValue(), toMb)));
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-p2cFlow", MathUtil.reserveTwoDigits(p2cFlowAdder.longValue(), toMb)));
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-relayFlow", MathUtil.reserveTwoDigits(relayFlowAdder.longValue(), toMb)));
        }, startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            List<WarnIncreaseRate> warnIncreaseRates = warnInfoService.getWarnIncreaseRate(clusterIds);
            transform(warnIncreaseRates);
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-warnIncreaseRates", warnIncreaseRates));
        }, startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            MessageQuality result = serverTrafficService.getMessageQualityByClusterIds(DateUtil.today(), clusterIds);
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-messageQuality", result));
        }, startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            List<UserLocationVo> location0 = historicalUserLocationService.getData();
            List<UserLocationVo> location1 = activeUserLocationService.getData();
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-historicalUserLocation", location0));
            messageSender.sendTextMessage(userId, new MessageVo<>("statistics-activeUserLocation", location1));
        }, startTime, DEFAULT_DELAY));
        return list;
    }

    public void addStatisticsTask(Integer userId, Integer clusterId) {
        List<Integer> clusterIds = new ArrayList<>();
        if (Objects.equals(clusterId, 0)) {
            User user = userService.getById(userId);
            List<Integer> ids;
            if (user.getUserType() == UserType.ADMIN) {
                ids = clusterService.getIds();
            } else {
                ids = clusterService.getByUserId(userId);
            }
            clusterIds.addAll(ids);
        } else {
            clusterIds.add(clusterId);
        }
        taskMap.put(userId, addStatisticsTask(clusterIds, userId));
        log.info("userId={}成功添加统计页面定时任务", userId);
    }

    public void addMonitorTask(Integer userId, Integer clusterId, Integer serverId) {
        taskMap.put(userId, doAddMonitorTask(userId, clusterId, serverId));
        log.info("userId={}成功添加监控页面定时任务", userId);
    }

    private List<ScheduledFuture<?>> doAddMonitorTask(Integer userId, Integer clusterId, Integer serverId) {
        val list = new ArrayList<ScheduledFuture<?>>();
        val startTime = DateUtils.addMilliseconds(new Date(), FIRST_DELAY);
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            //游戏端连接
            val clientConnectionList = new ArrayList<ClientConnectVo>();
            //服务器连接
            val serverConnectionList = new ArrayList<ServerConnectVo>();
            List<ServerTrafficConnectionVo> serverTrafficConnectionVos = serverTrafficService.getConnectionData(tuple().get(0), clusterId, serverId);
            serverTrafficConnectionVos.forEach(connection -> {
                val clientConnection = new ClientConnectVo();
                clientConnection.setCreateTime(connection.getCreateTime());
                clientConnection.setPlatformC2pConnectActiveTotal(connection.getPlatformC2pConnectActiveTotal());
                clientConnection.setPlatformC2pConnectInactiveTotal(connection.getPlatformC2pConnectInactiveTotal());
                clientConnection.setPlatformC2pConnectErrorTotal(connection.getPlatformC2pConnectErrorTotal());
                clientConnection.setPlatformC2pConnectIdleCloseTotal(connection.getPlatformC2pConnectIdleCloseTotal());
                clientConnectionList.add(clientConnection);
                val serverConnectionVo = new ServerConnectVo();
                serverConnectionVo.setCreateTime(connection.getCreateTime());
                serverConnectionVo.setPlatformS2pConnectActiveTotal(connection.getPlatformS2pConnectActiveTotal());
                serverConnectionVo.setPlatformS2pConnectInactiveTotal(connection.getPlatformS2pConnectInactiveTotal());
                serverConnectionVo.setPlatformS2pConnectErrorTotal(connection.getPlatformS2pConnectErrorTotal());
                serverConnectionVo.setPlatformS2pConnectIdleCloseTotal(connection.getPlatformS2pConnectIdleCloseTotal());
                serverConnectionList.add(serverConnectionVo);
            });
            messageSender.sendTextMessage(userId, new MessageVo<>("monitor-clientConnectVos", clientConnectionList));
            messageSender.sendTextMessage(userId, new MessageVo<>("monitor-serverConnectVos", serverConnectionList));
        }, startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> messageSender.sendTextMessage(userId, new MessageVo<>("monitor-forwardStatus", reportMinuteDataService.getForwardStatus(clusterId, serverId, tuple().get(0), tuple().get(1)))), startTime
                , DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> messageSender.sendTextMessage(userId, new MessageVo<>("monitor-trafficConnectionFlowVo", serverTrafficService.getMonitorData(clusterId, serverId, tuple().get(0), tuple().get(1)))), startTime,
                DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> messageSender.sendTextMessage(userId, new MessageVo<>("monitor-dayTotalTraffic", serverTrafficService.getDayTotalTraffic(clusterId, serverId, tuple().get(0)))), startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> messageSender.sendTextMessage(userId, new MessageVo<>("monitor-monitorTps", reportMinuteDataService.getMonitorTps(clusterId, serverId, tuple().get(0), tuple().get(1)))), startTime,
                DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> messageSender.sendTextMessage(userId, new MessageVo<>("monitor-activeServerCount", serverService.getUpServerCountByClusterId(clusterId))), startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> messageSender.sendTextMessage(userId, new MessageVo<>("monitor-totalServerCount", serverService.getTotalServerCountByClusterId(clusterId))), startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> messageSender.sendTextMessage(userId, new MessageVo<>("monitor-monitorTpsQpsPeak", reportMinuteDataService.getMonitorTpsQpsPeak(clusterId, serverId, tuple().get(0)))), startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            List<WarnHistoryInfo> warnHistoryInfoList = warnInfoService.getWarnInfoList(clusterId, serverId, tuple().get(0));
            if (!warnHistoryInfoList.isEmpty()) {
                val map = new HashMap<Integer, LongAdder>(16);
                warnHistoryInfoList.forEach(x -> map.compute(x.getWarnLevel(), (k, v) -> {
                    if (Objects.isNull(v)) {
                        v = new LongAdder();
                    }
                    v.increment();
                    return v;
                }));
                messageSender.sendTextMessage(userId, new MessageVo<>("monitor-warnLevelDetails", map));
                int numToReturn = 20;
                List<WarnHistoryInfo> warnHistoryInfoList1;
                if (warnHistoryInfoList.size() <= numToReturn) {
                    warnHistoryInfoList1 = warnHistoryInfoList;
                } else {
                    warnHistoryInfoList1 = warnHistoryInfoList.subList(0, numToReturn + 1);
                }
                final WarnType[] values = WarnType.values();
                warnHistoryInfoList1.forEach(warnHistoryInfo -> warnHistoryInfo.setShortName(getShortName(warnHistoryInfo.getWarnType(), values)));
                messageSender.sendTextMessage(userId, new MessageVo<>("monitor-warnHistoryInfoList", warnHistoryInfoList1));
            }
        }, startTime, DEFAULT_DELAY));
        list.add(taskScheduler.scheduleWithFixedDelay(() -> {
            MessageRateVo vo = serverTrafficService.getMessageRate(clusterId, serverId, tuple().get(0), tuple().get(1));
            vo.setClientFailedRate(MathUtil.reserveTwoDigits(vo.getClientFailedTimes(), vo.getClientSendTimes()));
            vo.setServerFailedRate(MathUtil.reserveTwoDigits(vo.getServerFailedTimes(), vo.getServerSendTimes()));
            messageSender.sendTextMessage(userId, new MessageVo<>("monitor-messageRateVo", vo));
        }, startTime, DEFAULT_DELAY));
        return list;
    }

    private Tuple tuple() {
        String dateTime = DateUtil.format(DateUtils.addMinutes(new Date(), -2), "yyyy-MM-dd HH:mm");
        val arr = dateTime.split(" ");
        return new Tuple(arr[0], arr[1]);
    }

    private String getShortName(int index, WarnType[] warnTypes) {
        final Optional<WarnType> option = Arrays.stream(warnTypes).filter(x -> Objects.equals(x.getIndex(), index)).findFirst();
        return option.isPresent() ? option.get().getShortName() : "";
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
