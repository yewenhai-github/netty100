package com.netty100.service.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.Cluster;
import com.netty100.entity.ReportMinuteData;
import com.netty100.entity.Server;
import com.netty100.entity.ServerTraffic;
import com.netty100.enumeration.ServerStatus;
import com.netty100.mapper.ServerMapper;
import com.netty100.pojo.dto.*;
import com.netty100.pojo.vo.ServerBriefVo;
import com.netty100.pojo.vo.ServerListVo;
import com.netty100.pojo.vo.ServerVo;
import com.netty100.pojo.vo.TrafficTotalVo;
import com.netty100.service.ClusterService;
import com.netty100.service.ReportMinuteDataService;
import com.netty100.service.ServerService;
import com.netty100.service.ServerTrafficService;
import com.netty100.utils.MathUtil;
import com.netty100.utils.RedisTool;
import com.netty100.utils.exception.CommonException;
import com.netty100.utils.respons.ResponseMsg;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
@Slf4j
@Service
public class ServerServiceImpl implements ServerService {

    private ClusterService clusterService;

    private ServerMapper serverMapper;

    private ServerTrafficService serverTrafficService;

    private ReportMinuteDataService reportMinuteDataService;

    private ThreadPoolTaskExecutor executor;

    private RedisTool redisTool;

    @Autowired
    public void setRedisTool(RedisTool redisTool) {
        this.redisTool = redisTool;
    }

    @Autowired
    public void setExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setReportMinuteDataService(ReportMinuteDataService reportMinuteDataService) {
        this.reportMinuteDataService = reportMinuteDataService;
    }

    @Autowired
    public void setServerMapper(ServerMapper serverMapper) {
        this.serverMapper = serverMapper;
    }

    @Autowired
    public void setServerMessageService(ServerTrafficService serverTrafficService) {
        this.serverTrafficService = serverTrafficService;
    }

    @Autowired
    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bootReport(BootReportDto dto) {
        executor.execute(() -> {
            final Server server = this.getOne(dto.getIntranetIp(), dto.getPort());
            if (Objects.isNull(server)) {
                log.error("netty节点:{}不存在", dto.getIntranetIp());
                throw new CommonException(ResponseMsg.SERVER_NOT_EXISTS);
            }
            server.setLastBootTime(dto.getLastBootTime());
            server.setServerStatus(ServerStatus.UP);
            this.update(server);
            val vo = getServerVoSetByIds(Collections.singletonList(server.getId()));
            redisTool.markServerUp(vo);
            log.info("netty节点:{}启动上报处理成功", dto.getIntranetIp());
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(Server server) {
        return serverMapper.save(server);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServerVo> getServers(Integer clusterId) {
        return serverMapper.getServers(clusterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Server> findDead(int timeout, String nowTime) {
        return serverMapper.findDead(timeout, nowTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getClusterServerStatus(Integer clusterId) {
        return serverMapper.getClusterServerStatus(clusterId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Integer serverId) {
        return serverMapper.deleteById(serverId);
    }

    @Override
    @Transactional(readOnly = true)
    public Server getOne(String intranetIp, String port) {
        return serverMapper.getOne(intranetIp, port);
    }

    /**
     * 连接数上报
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportConnectionCount(ReportConnectionCountDto dto) {
        executor.execute(() -> {
            final Server server = this.getOne(dto.getIntranetIp(), dto.getPort());
            if (Objects.isNull(server)) {
                log.error("netty节点:{}不存在", dto.getIntranetIp());
                throw new CommonException(ResponseMsg.SERVER_NOT_EXISTS);
            }
            //更新心跳时间
            server.setLastHeartBeatTime(new Date());
            server.setClientConnectCount(dto.getClientConnectCount());
            server.setServerConnectCount(dto.getServerConnectCount());
            this.updateLastHeartBeatTime(server);
            val vo = getServerVoSetByIds(Collections.singletonList(server.getId()));
            redisTool.markServerUp(vo);
            //保存连接数信息
            val time = DateUtil.format(DateUtils.addMinutes(new Date(), -1), "yyyy-MM-dd HH:mm");
            val timeArr = time.split(" ");
            ReportMinuteData entity = new ReportMinuteData();
            entity.setServerId(server.getId());
            entity.setClusterId(server.getClusterId());
            entity.setCreateDate(timeArr[0]);
            entity.setCreateTime(timeArr[1]);
            entity.setServerConnectCount(dto.getServerConnectCount());
            entity.setClientConnectCount(dto.getClientConnectCount());
            reportMinuteDataService.saveConnectionCount(entity);
            log.info("netty节点:{}心跳上报处理成功", dto.getIntranetIp());
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLastHeartBeatTime(Server server) {
        serverMapper.updateLastHeartBeatTime(server);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addServer(AddServerDto dto) {
        final Cluster cluster = clusterService.getById(dto.getClusterId());
        if (Objects.isNull(cluster)) {
            log.error("netty节点:{}不存在", dto.getIntranetIp());
            throw new CommonException(ResponseMsg.CLUSTER_NOT_EXIST);
        }
        Server server = this.getOne(dto.getIntranetIp(), dto.getPort());
        if (Objects.nonNull(server)) {
            throw new CommonException(ResponseMsg.SERVER_ALREADY_EXISTS);
        }
        server = new Server();
        BeanUtils.copyProperties(dto, server);
        server.setServerStatus(ServerStatus.DOWN);
        server.setBootTimes(0);
        return this.save(server);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Server server) {
        return serverMapper.update(server);
    }

    private int getSecondUntil(Date date) {
        int hour = DateUtil.hour(date, true);
        int min = DateUtil.minute(date);
        int seconds = DateUtil.second(date);
        return hour * 3600 + min * 60 + seconds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportMessage(ReportMessageDto dto) {
        executor.execute(() -> {
            Server server = this.getOne(dto.getIntranetIp(), dto.getPort());
            if (Objects.isNull(server)) {
                log.error("netty节点:{}不存在", dto.getIntranetIp());
                throw new CommonException(ResponseMsg.SERVER_NOT_EXISTS);
            }
            val serverTraffic = new ServerTraffic();
            BeanUtils.copyProperties(dto, serverTraffic);
            Date date = DateUtils.addMinutes(new Date(), -1);
            String dateFormat = DateUtil.format(date, DateUtil.newSimpleFormat("yyyy-MM-dd HH:mm"));
            val timeArr = dateFormat.split(" ");
            serverTraffic.setCreateDate(timeArr[0]);
            serverTraffic.setCreateTime(timeArr[1]);
            serverTraffic.setServerId(server.getId());
            serverTraffic.setClusterId(server.getClusterId());
            serverTrafficService.save(serverTraffic);
            ReportMinuteData data = new ReportMinuteData();
            data.setCreateDate(timeArr[0]);
            data.setCreateTime(timeArr[1]);
            data.setServerId(server.getId());
            data.setClusterId(server.getClusterId());
            //获取总消息次数,游戏端投递成功次数+游戏端投递失败次数+服务器投递成功次数+服务器投递失败次数
            long total = dto.getPlatformC2pMessageReadSuccessTotal() + dto.getPlatformC2pMessageReadFailTotal()
                    + dto.getPlatformS2pMessageReadSuccessTotal() + dto.getPlatformS2pMessageReadFailTotal();
            //获取投递失败次数,游戏端投递失败次数+服务器投递失败次数
            long failed = dto.getPlatformC2pMessageReadFailTotal() + dto.getPlatformS2pMessageReadFailTotal();
            //投递失败率,已经是百分比形式,保留两位有效数字,包含游戏端和服务器
            BigDecimal failedMessageRate = MathUtil.reserveTwoDigits(failed, total);
            data.setFailedRate(failedMessageRate);
            //节点消息转发率,已经是百分比形式,保留两位有效数据
            Integer platformP2pMessageRelayTotal;
            if (dto.getPlatformP2pMessageRelayTotal() >= dto.getPlatformS2pMessageReadSuccessTotal() + dto.getPlatformC2pMessageReadSuccessTotal()) {
                platformP2pMessageRelayTotal = dto.getPlatformS2pMessageReadSuccessTotal() + dto.getPlatformC2pMessageReadSuccessTotal();
            } else {
                platformP2pMessageRelayTotal = dto.getPlatformP2pMessageRelayTotal();
            }
            val forwardMessageRate = MathUtil.reserveTwoDigits(platformP2pMessageRelayTotal, dto.getPlatformS2pMessageReadSuccessTotal() + dto.getPlatformC2pMessageReadSuccessTotal());
            data.setForwardTimes(dto.getPlatformP2pMessageRelayTotal());
            data.setForwardRate(forwardMessageRate);
            final int seconds = 60;
            data.setC2pTps(getResult(dto.getPlatformC2pMessageReadSuccessTotal() + dto.getPlatformC2pMessageReadFailTotal(), seconds));
            data.setP2sTps(getResult(dto.getPlatformC2pMessageReadSuccessTotal(), seconds));
            data.setS2pTps(getResult(dto.getPlatformS2pMessageReadSuccessTotal() + dto.getPlatformS2pMessageReadFailTotal(), seconds));
            data.setP2cTps(getResult(dto.getPlatformS2pMessageReadSuccessTotal(), seconds));
            //获取当前时间距离0点的秒数
            val secondsUntil = getSecondUntil(date);
            final TrafficTotalVo trafficTotalVo = serverTrafficService.getTotal(server.getId(), timeArr[0]);
            trafficTotalVo.setC2pTotal(trafficTotalVo.getC2pTotal() + dto.getPlatformC2pMessageReadSuccessTotal() + dto.getPlatformC2pMessageReadFailTotal());
            trafficTotalVo.setP2sTotal(trafficTotalVo.getP2sTotal() + dto.getPlatformC2pMessageReadSuccessTotal());
            trafficTotalVo.setS2pTotal(trafficTotalVo.getS2pTotal() + dto.getPlatformS2pMessageReadSuccessTotal() + dto.getPlatformS2pMessageReadFailTotal());
            trafficTotalVo.setP2cTotal(trafficTotalVo.getP2cTotal() + dto.getPlatformS2pMessageReadSuccessTotal());
            final List<Integer> qps = getQps(trafficTotalVo, secondsUntil);
            int index = 0;
            data.setC2pQps(qps.get(index));
            index++;
            data.setP2sQps(qps.get(index));
            index++;
            data.setS2pQps(qps.get(index));
            index++;
            data.setP2cQps(qps.get(index));
            reportMinuteDataService.saveTrafficFlowStatus(data);
            log.info("netty节点:{}流量,连接情况上报处理成功", dto.getIntranetIp());
        });
    }

    private List<Integer> getQps(TrafficTotalVo total, int secondsUntil) {
        val list = new ArrayList<Integer>();
        val list1 = Arrays.asList(total.getC2pTotal(), total.getP2sTotal(), total.getS2pTotal(), total.getP2cTotal());
        list1.forEach(element -> list.add(getResult(element.intValue(), secondsUntil)));
        return list;
    }

    private int getResult(int x, int y) {
        return (int) Math.ceil((double) x / y);
    }

    @Override
    @Transactional(readOnly = true)
    public Server getById(Integer serverId) {
        return serverMapper.getById(serverId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Server> findOpenMonitorServers() {
        return serverMapper.findOpenMonitorServers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServerVo> getServers0(Integer id) {
        final List<ServerVo> list = serverMapper.getServers0(id);
//        for (ServerVo serverVo : list) {
//            ReportMinuteData latest = reportMinuteDataService.getLatest(serverVo.getId());
//            Integer clientConnectCount = Objects.isNull(latest) ? 0 : latest.getClientConnectCount() == null ? 0 : latest.getClientConnectCount();
//            Integer serverConnectCount = Objects.isNull(latest) ? 0 : latest.getServerConnectCount() == null ? 0 : latest.getServerConnectCount();
//            serverVo.setClientConnectCount(clientConnectCount);
//            serverVo.setServerConnectCount(serverConnectCount);
//        }
        return list;
    }

    @Override
    public int getUpServerCountByClusterId(Integer clusterId) {
        return serverMapper.getUpServerCountByClusterId(clusterId);
    }

    @Override
    public int getTotalServerCountByClusterId(Integer clusterId) {
        return serverMapper.getTotalServerCountByClusterId(clusterId);
    }

    @Override
    public PageInfo<ServerListVo> getServerPage(ServerListDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        final PageInfo<ServerListVo> page = new PageInfo<>(serverMapper.getServerList(dto.getClusterId()));
        page.getList().forEach(element -> {
            if (element.getServerStatus() == ServerStatus.DOWN) {
                element.setServerConnectionCount(0);
                element.setClientConnectionCount(0);
            }
//            else {
//                ReportMinuteData data = reportMinuteDataService.getLatest(element.getId());
//                int serverConnectCount = Objects.isNull(data) ? 0 : Objects.isNull(data.getServerConnectCount()) ? 0 : data.getServerConnectCount();
//                int clientConnectCount = Objects.isNull(data) ? 0 : Objects.isNull(data.getClientConnectCount()) ? 0 : data.getClientConnectCount();
//                element.setClientConnectionCount(clientConnectCount);
//                element.setServerConnectionCount(serverConnectCount);
//            }
        });
        return page;
    }

    @Override
    public List<ServerBriefVo> briefServerList(Integer clusterId) {
        return serverMapper.briefServerList(clusterId);
    }

    @Override
    public void markServerShutdown(Server server) {
        serverMapper.markServerShutdown(server);
    }

    @Override
    public List<ServerVo> getAllActiveServers() {
        return serverMapper.getAllActiveServers();
    }

    @Override
    public Set<ServerVo> getServerVoSetByIds(List<Integer> ids) {
        return serverMapper.getServerVoSetByIds(ids);
    }

}
