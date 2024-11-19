package com.netty100.timer;

import cn.hutool.core.collection.CollectionUtil;
import com.netty100.entity.Cluster;
import com.netty100.entity.Server;
import com.netty100.enumeration.ServerStatus;
import com.netty100.enumeration.WarnType;
import com.netty100.pojo.dto.ServerWarnContentDto;
import com.netty100.pojo.vo.ServerVo;
import com.netty100.service.ClusterService;
import com.netty100.service.CommonWarnSendService;
import com.netty100.service.ServerService;
import com.netty100.timer.config.TaskConfig;
import com.netty100.utils.DateUtils;
import com.netty100.utils.RedisTool;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description 服务节点心跳监控任务
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/25
 */
@Slf4j
@Component
public class ServerStatusMonitorTask {
    @Autowired
    private ServerService serverService;
    @Autowired
    private ClusterService clusterService;
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private CommonWarnSendService commonWarnSendService;
    @Autowired
    private RedisTool redisTool;

    @Scheduled(cron = "30 * * * * *")
    @SchedulerLock(name = "serverStatusMonitorTask", lockAtMostFor = 2000, lockAtLeastFor = 2000)
    public void serverStatusMonitor() {
        log.info("=====================serverStatusMonitorTask-->start======================================");
        log.info("current time :{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        executor.execute(() -> {
            String now = DateUtils.getFormatNow(DateUtils.YMD_DASH_BLANK_HMS_COLON);
            List<Server> servers = serverService.findDead(TaskConfig.DEAD_TIMEOUT, now);
            if (CollectionUtil.isEmpty(servers)) {
                return;
            }
            List<ServerWarnContentDto> warnContents = servers.stream().map(server -> {
                serverService.markServerShutdown(server);
                Cluster cluster = clusterService.getById(server.getClusterId());
                String content = String.format(
                        TaskConfig.downContent,
                        Objects.nonNull(cluster) ? cluster.getCluster() : "",
                        server.getId(),
                        server.getIntranetIp());
                return new ServerWarnContentDto(server, content);
            }).collect(Collectors.toList());
            redisTool.markServerDown(servers.stream().map(x -> {
                val vo = new ServerVo();
                vo.setId(x.getId());
                return vo;
            }).collect(Collectors.toList()));
            log.info("执行告警的服务节点系信息:{}", servers.parallelStream().map(Server::getId).map(String::valueOf).collect(Collectors.joining(",")));
            // 告警
            commonWarnSendService.warnServerSend(warnContents, WarnType.SERVER_DOWN, null);
            List<Integer> clusterIds = servers.stream().map(Server::getClusterId).distinct().collect(Collectors.toList());
            List<Cluster> clusters = new ArrayList<>();
            clusterIds.forEach(id -> {
                List<Integer> serverStatusList = serverService.getClusterServerStatus(id);
                Integer status;
                if (serverStatusList.size() == 1
                        && Objects.nonNull(status = serverStatusList.get(0))
                        && Objects.equals(status, ServerStatus.DOWN)) {
                    clusters.add(clusterService.getById(id));
                }
            });
            if (CollectionUtil.isNotEmpty(clusters)) {
                log.info("集群执行告警:{}", clusters.parallelStream().map(Cluster::getCluster).collect(Collectors.joining(",")));
                commonWarnSendService.warnClusterSend(clusters, WarnType.CLUSTER_DOWN);
            }
        });
        log.info("===============================end=====================================");
    }
}
