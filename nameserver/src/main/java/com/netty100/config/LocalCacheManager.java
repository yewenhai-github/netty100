package com.netty100.config;

import com.netty100.entity.AppConfig;
import com.netty100.entity.Cluster;
import com.netty100.entity.Protocol;
import com.netty100.pojo.vo.ServerVo;
import com.netty100.service.AppConfigService;
import com.netty100.service.ClusterService;
import com.netty100.service.ProtocolService;
import com.netty100.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author why
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocalCacheManager {

    private volatile List<Cluster> clusters;

    private volatile List<ServerVo> servers;

    private volatile Map<String, List<Protocol>> protocols;

    private volatile List<AppConfig> appConfigs;

    private final ClusterService clusterService;

    private final ServerService serverService;

    private final ProtocolService protocolService;

    private final AppConfigService appConfigService;

    public List<AppConfig> appConfigs() {
        return this.appConfigs;
    }

    public List<Cluster> clusters() {
        return this.clusters;
    }

    public List<ServerVo> servers() {
        return this.servers;
    }

    public Map<String, List<Protocol>> protocols() {
        return this.protocols;
    }

    @Async
    @Scheduled(initialDelay = -1,fixedDelay = 10000)
    public void loadClusters() {
        this.clusters = clusterService.queryAll();
    }

    @Async
    @Scheduled(initialDelay = -1,fixedDelay = 10000)
    public void loadServers() {
        this.servers = serverService.getAllActiveServers();
    }

    @Async
    @Scheduled(initialDelay = -1,fixedDelay = 10000)
    public void loadProtocols() {
        this.protocols = protocolService.getAll();
    }

    @Async
    @Scheduled(initialDelay = -1,fixedDelay = 10000)
    public void loadAppConfigs() {
        this.appConfigs = appConfigService.getAll();
    }
}
