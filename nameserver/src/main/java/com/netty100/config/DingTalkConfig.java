package com.netty100.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author why
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ding-talk")
public class DingTalkConfig {

    private String secret;

    private String token;
}
