package com.netty100.timer;

import com.netty100.service.ReportMinuteDataService;
import com.netty100.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description 定时器-->消息流量告警
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServerReconnectMonitorTask {

    private final ReportMinuteDataService reportMinuteDataService;

    @Scheduled(cron = "30 */5 * * * *")
    @SchedulerLock(name = "serverReconnectMonitorTask", lockAtMostFor = 18000, lockAtLeastFor = 18000)
    public void serverReconnectMonitor() {
        log.info("======================serverReconnectMonitor-->start======================================");
        log.info("current time :{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.YMD_DASH_BLANK_HMS_COLON)));
        reportMinuteDataService.serverReconnectCheck();
    }
}
