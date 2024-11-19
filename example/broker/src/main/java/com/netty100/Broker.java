package com.netty100;

import com.netty100.broker.annotation.EnableWhyBroker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableWhyBroker
@SpringBootApplication
public class Broker {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Broker.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

}
