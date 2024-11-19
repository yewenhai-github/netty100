package com.netty100.netty.codec.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Data
@ConfigurationProperties(prefix = "com.netty100.producer")
@Component
public class WhyNettyProducerProperties {


}
