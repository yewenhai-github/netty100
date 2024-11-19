package com.netty100.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author why
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {RedissonSingleProperties.class, RedissonCommonProperties.class})
public class RedissonConfiguration {

    private final RedissonSingleProperties redissonSingleProperties;

    private final RedissonCommonProperties redissonCommonProperties;

    @Bean(destroyMethod = "shutdown")
    @SneakyThrows
    @ConditionalOnProperty(prefix = "netty100.redisson.single", name = "enable", havingValue = "true")
    public RedissonClient single() {
        Config config = new Config();
        BeanUtils.copyProperties(redissonCommonProperties.getConfig(), config);
        final SingleServerConfig singleServerConfig = config.useSingleServer();
        final RedissonSingleProperties.SingleConfig configuration = redissonSingleProperties.getConfig();
        BeanUtils.copyProperties(configuration, singleServerConfig);
        log.info("redisson-single模式启动,详细配置 :{}", config.toYAML());
        return Redisson.create(config);
    }
}
