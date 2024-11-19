package com.netty100.broker.autoconfig;

import com.netty100.broker.annotation.EnableWhyBroker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Configuration
@EnableConfigurationProperties(WhyKernelProperties.class)
@ConditionalOnBean(annotation = EnableWhyBroker.class)
public class WhyKernelImport {


}
