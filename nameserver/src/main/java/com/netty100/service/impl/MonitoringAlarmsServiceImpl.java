package com.netty100.service.impl;

import cn.hutool.core.date.DateUtil;
import com.netty100.entity.WarnHistoryInfo;
import com.netty100.enumeration.WarnType;
import com.netty100.pojo.dto.MonitorDto;
import com.netty100.pojo.vo.*;
import com.netty100.service.*;
import com.netty100.utils.MathUtil;
import lombok.val;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author why
 */
@SuppressWarnings("DuplicatedCode")
@Service
public class MonitoringAlarmsServiceImpl implements MonitoringAlarmsService {

    private WarnInfoService warnInfoService;

    private ReportMinuteDataService reportMinuteDataService;

    private ServerTrafficService serverTrafficService;

    private ServerService serverService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonitorResultVo getMonitorData(MonitorDto dto) {
        val result = new MonitorResultVo();
        String dateTime = DateUtil.format(DateUtils.addMinutes(new Date(), -2), "yyyy-MM-dd HH:mm");
        val arr = dateTime.split(" ");
        val date = arr[0];
        val time = arr[1];
        //游戏端连接
        val clientConnectionList = new ArrayList<ClientConnectVo>();
        //服务器连接
        val serverConnectionList = new ArrayList<ServerConnectVo>();
        List<ServerTrafficConnectionVo> serverTrafficConnectionVos = serverTrafficService.getConnectionData(date, dto.getClusterId(), dto.getServerId());
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
        result.setClientConnectVos(clientConnectionList);
        result.setServerConnectVos(serverConnectionList);
        //转发率,如果选择节点,则显示节点数据,如果未选择节点,显示集群数据,同时显示转发次数
        ForwardStatus forwardStatus = reportMinuteDataService.getForwardStatus(dto.getClusterId(), dto.getServerId(), date, time);
        result.setForwardStatus(forwardStatus);
        //上一分钟的异常断开次数(App端)
        TrafficConnectionFlowVo trafficConnectionFlowVo = serverTrafficService.getMonitorData(dto.getClusterId(), dto.getServerId(), date, time);
        result.setTrafficConnectionFlowVo(trafficConnectionFlowVo);
        //当天的累计流量
        BigInteger dayTotalTraffic = serverTrafficService.getDayTotalTraffic(dto.getClusterId(), dto.getServerId(), date);
        result.setDayTotalTraffic(dayTotalTraffic);
        //服务器连接数,游戏端连接数,tps,如果选择节点,则显示节点数据,如果未选择节点,显示集群数据
        MonitorTps monitorTps = reportMinuteDataService.getMonitorTps(dto.getClusterId(), dto.getServerId(), date, time);
        result.setMonitorTps(monitorTps);
        //平台节点的状况
        int activeServerCount = serverService.getUpServerCountByClusterId(dto.getClusterId());
        result.setActiveServerCount(activeServerCount);
        int totalServerCount = serverService.getTotalServerCountByClusterId(dto.getClusterId());
        result.setTotalServerCount(totalServerCount);
        //tps峰值
        //qps峰值
        MonitorTpsQpsPeak monitorTpsQpsPeak = reportMinuteDataService.getMonitorTpsQpsPeak(dto.getClusterId(), dto.getServerId(), date);
        result.setMonitorTpsQpsPeak(monitorTpsQpsPeak);
        //告警等级
        List<WarnHistoryInfo> warnHistoryInfoList = warnInfoService.getWarnInfoList(dto.getClusterId(), dto.getServerId(), date);
        if (!warnHistoryInfoList.isEmpty()) {
            val map = new HashMap<Integer, LongAdder>(16);
            warnHistoryInfoList.forEach(x -> map.compute(x.getWarnLevel(), (k, v) -> {
                if (Objects.isNull(v)) {
                    v = new LongAdder();
                }
                v.increment();
                return v;
            }));
            result.setWarnLevelDetails(map);
            //新增告警
            int numToReturn = 20;
            if (warnHistoryInfoList.size() <= numToReturn) {
                result.setWarnHistoryInfoList(warnHistoryInfoList);
            } else {
                result.setWarnHistoryInfoList(warnHistoryInfoList.subList(0, numToReturn + 1));
            }
            final WarnType[] values = WarnType.values();
            result.getWarnHistoryInfoList().forEach(warnHistoryInfo -> warnHistoryInfo.setShortName(getShortName(warnHistoryInfo.getWarnType(), values)));
        }
        //消息质量检测
        MessageRateVo vo = serverTrafficService.getMessageRate(dto.getClusterId(), dto.getServerId(), date, time);
        vo.setClientFailedRate(MathUtil.reserveTwoDigits(vo.getClientFailedTimes(), vo.getClientSendTimes()));
        vo.setServerFailedRate(MathUtil.reserveTwoDigits(vo.getServerFailedTimes(), vo.getServerSendTimes()));
        result.setMessageRateVo(vo);
        return result;
    }

    private String getShortName(int index, WarnType[] warnTypes) {
        final Optional<WarnType> option = Arrays.stream(warnTypes).filter(x -> Objects.equals(x.getIndex(), index)).findFirst();
        return option.isPresent() ? option.get().getShortName() : "";
    }

    @Autowired
    public void setWarnInfoService(WarnInfoService warnInfoService) {
        this.warnInfoService = warnInfoService;
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
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }
}
