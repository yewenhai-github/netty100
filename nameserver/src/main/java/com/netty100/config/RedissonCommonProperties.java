package com.netty100.config;

import lombok.Getter;
import lombok.Setter;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 通用配置,适用于所有的redis组态
 *
 * @author why
 */

@ConfigurationProperties(prefix = "netty100.redisson.common")
public class RedissonCommonProperties {

    @Getter
    private final CommonConfig config = new CommonConfig();

    @Setter
    @Getter
    public static class CommonConfig {

        private int threads = 8;

        private int nettyThreads = 8;

        private TransportMode transportMode = TransportMode.NIO;

        private long lockWatchdogTimeout = 30000;

        private boolean keepPubSubOrder = true;
    }
}
