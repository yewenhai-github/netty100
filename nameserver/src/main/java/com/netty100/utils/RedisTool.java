package com.netty100.utils;

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
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.*;

/**
 * @author why
 */
@Slf4j
@RequiredArgsConstructor
public class RedisTool {

    String clusterKey = "netty100:cluster2";
    String serversKey = "netty100:servers2";
    String protocolsKey = "netty100:protocols2";
    String appConfigsKey = "netty100:appConfigs2";
    private final ClusterService clusterService;
    private final ServerService serverService;
    private final ProtocolService protocolService;
    private final AppConfigService appConfigService;
    private final RedissonClient redissonClient;

    public void cacheData() {
        try {
            final RBucket<List<Cluster>> bucket = redissonClient.getBucket(clusterKey);
            bucket.set(clusterService.queryAll());
            final RBucket<HashSet<ServerVo>> bucket1 = redissonClient.getBucket(serversKey);
            bucket1.set(new HashSet<>(serverService.getAllActiveServers()));
            final RBucket<Map<String, List<Protocol>>> bucket2 = redissonClient.getBucket(protocolsKey);
            bucket2.set(protocolService.getAll());
            final RBucket<List<AppConfig>> bucket3 = redissonClient.getBucket(appConfigsKey);
            bucket3.set(appConfigService.getAll());
        } catch (Exception e) {
            log.error("智能路由加载缓存失败", e);
        }
    }

    public void markServerDown(List<ServerVo> servers) {
        try {
            final RBucket<HashSet<ServerVo>> bucket = redissonClient.getBucket(serversKey);
            servers.forEach(bucket.get()::remove);
        } catch (Exception e) {
            log.error("智能路由缓存移除指定节点失败", e);
        }
    }

    public void markServerUp(Collection<ServerVo> servers) {
        try {
            final RBucket<HashSet<ServerVo>> bucket = redissonClient.getBucket(serversKey);
            bucket.get().addAll(servers);
        } catch (Exception e) {
            log.error("智能路由缓存添加指定节点失败", e);
        }
    }

    public List<Cluster> clusters() {
        return redissonClient.<List<Cluster>>getBucket(clusterKey).get();
    }

    public Set<ServerVo> servers() {
        return redissonClient.<HashSet<ServerVo>>getBucket(serversKey).get();

    }

    public Map<String, List<Protocol>> protocols() {
        return redissonClient.<Map<String, List<Protocol>>>getBucket(protocolsKey).get();
    }

    public List<AppConfig> appConfigs() {
        return redissonClient.<List<AppConfig>>getBucket(appConfigsKey).get();
    }
}
