package com.netty100.timer;

import com.netty100.service.ServerTrafficService;
import com.netty100.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description
 * @Author why
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServerMessageThresholdMonitorTask {

    public final ServerTrafficService serverTrafficService;

    @Scheduled(cron = "10 */5 * * * ? ")
    @SchedulerLock(name = "serverMessageThresholdMonitorTask", lockAtMostFor = 18000, lockAtLeastFor = 18000)
    public void serverMessageThresholdMonitor() {
        log.info("======================serverMessageThresholdMonitor-->start======================================");
        log.info("serverMessageThresholdMonitor ==>current time :{}",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.YMD_DASH_BLANK_HMS_COLON)));
        serverTrafficService.serverMessageThresholdMonitor();
        log.info("======================serverMessageThresholdMonitor-->start======================================");
    }
}
