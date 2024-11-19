package com.netty100.config.interceptor;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author why
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "netty100.auth.ignore")
public class IgnoredUrlsProperties {

    private List<String> paths;

}

