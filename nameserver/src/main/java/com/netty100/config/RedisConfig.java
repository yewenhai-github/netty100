package com.netty100.config;

import com.netty100.service.AppConfigService;
import com.netty100.service.ClusterService;
import com.netty100.service.ProtocolService;
import com.netty100.service.ServerService;
import com.netty100.utils.RedisTool;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author why
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisConfig {
    private final ClusterService clusterService;
    private final ServerService serverService;
    private final ProtocolService protocolService;
    private final AppConfigService appConfigService;
    private final RedissonClient redissonClient;

    @Bean
    public RedisTool redisTool() {
        return new RedisTool(clusterService, serverService, protocolService, appConfigService, redissonClient);
    }
}

