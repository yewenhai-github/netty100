package com.netty100.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author why
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "netty100.auth")
public class AuthTokenConfig {

    private String clientToken;

    private String serverToken;

    private String nodeToken;
}
