package com.netty100.config;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author why
 */
@Configuration
public class LocalCacheConfig {

    @Bean
    public ExpiringMap<String, String> devicePwdCache() {
        return ExpiringMap.builder().expiration(12, TimeUnit.HOURS)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .build();
    }
}
