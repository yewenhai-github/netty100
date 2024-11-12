package com.netty100;

import com.netty100.kernel.annotation.EnableWhyNettyKernel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableWhyNettyKernel
@SpringBootApplication
public class KernelAppRun {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(KernelAppRun.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

}
