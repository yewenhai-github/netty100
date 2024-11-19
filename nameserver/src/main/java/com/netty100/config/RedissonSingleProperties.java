package com.netty100.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author why
 */
@ConfigurationProperties(value = "netty100.redisson.single")
public class RedissonSingleProperties {

    @Getter
    @Setter
    private boolean enable = false;

    @Getter
    private final SingleConfig config = new SingleConfig();

    @Getter
    @Setter
    public static class SingleConfig {

        /**
         * Redis server address
         */
        private String address;

        /**
         * Minimum idle subscription connection amount
         */
        private int subscriptionConnectionMinimumIdleSize = 1;

        /**
         * Redis subscription connection maximum pool size
         */
        private int subscriptionConnectionPoolSize = 50;

        /**
         * Minimum idle Redis connection amount
         */
        private int connectionMinimumIdleSize = 24;

        /**
         * Redis connection maximum pool size
         */
        private int connectionPoolSize = 64;

        /**
         * Database index used for Redis connection
         */
        private int database = 0;

        /**
         * If pooled connection not used for a timeout time and current connections amount bigger
         * than minimum idle connections pool size,
         * then it will closed and removed from pool. Value in milliseconds
         */
        private int idleConnectionTimeout = 10000;

        /**
         * Timeout during connecting to any Redis server.
         * Value in milliseconds.
         */
        private int connectTimeout = 10000;

        /**
         * Redis server response timeout. Starts to countdown when Redis command was succesfully sent.
         * Value in milliseconds.
         */
        private int timeout = 3000;

        private int retryAttempts = 3;

        private int retryInterval = 1500;

        /**
         * Password for Redis authentication. Should be null if not needed
         */
        private String password;

        /**
         * Username for Redis authentication. Should be null if not needed
         */
        private String username;

        /**
         * Subscriptions per Redis connection limit
         */
        private int subscriptionsPerConnection = 5;

        /**
         * Enables TCP keepAlive for connection
         */
        private boolean keepAlive;

        /**
         * Enables TCP noDelay for connection
         */
        private boolean tcpNoDelay = true;
    }

}
