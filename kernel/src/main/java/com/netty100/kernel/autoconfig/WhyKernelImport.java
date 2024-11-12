package com.netty100.kernel.autoconfig;

import com.netty100.kernel.annotation.EnableWhyNettyKernel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Configuration
@EnableConfigurationProperties(WhyKernelProperties.class)
@ConditionalOnBean(annotation = EnableWhyNettyKernel.class)
public class WhyKernelImport {


}
