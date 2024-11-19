package com.netty100.config;

import cn.hutool.core.date.SystemClock;
import com.github.structlog4j.StructLog4J;
import com.github.structlog4j.json.JsonFormatter;
import com.netty100.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;


/**
 * @author why
 */
@Component
@RequiredArgsConstructor
@ConditionalOnClass(WebMvcConfigurer.class)
public class LogAutoConfig implements WebMvcConfigurer {

    @Value("${spring.application.name:NA}")
    private String appName;

    @Autowired
    private IpUtils ipUtils;

    @PostConstruct
    public void init() {
        StructLog4J.setFormatter(JsonFormatter.getInstance());
        StructLog4J.setMandatoryContextSupplier(() -> new Object[]{
                "host", ipUtils.getLocalAddress(),
                "appName", appName,
                "logTime", SystemClock.nowDate()
        });

    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void addInterceptors(InterceptorRegistry registry) {

    }
}
