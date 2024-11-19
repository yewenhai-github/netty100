package com.netty100;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author why
 */
@Slf4j
@SpringBootApplication
@MapperScan({"com.netty100.mapper"})
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class NameServer implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(NameServer.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("nameserver启动成功，接口地址：http://127.0.0.1:8090/doc.html");
    }
}