package com.netty100.netty.producer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Configuration
public class WhyMessageProducerAutoConfiguration {
    @Resource(name = "whyMessageProducerServiceImpl")
    private WhyMessageProducerServiceImpl whyMessageProducerServiceImpl;

    @Bean
    @Primary
    public WhyMessageProducerService topeMessageProducerService() {
        return whyMessageProducerServiceImpl;
    }
}
