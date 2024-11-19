package com.netty100.timer;

import com.netty100.service.WarnInfoService;
import com.netty100.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description 每天凌晨统计前天集群维度的预警数量  以预警类型分组
 * @Author why
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/31
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterWarnStatisticsTask {

    private final WarnInfoService warnInfoService;

    @Scheduled(cron = "0 20 0 * * ? ")
    @SchedulerLock(name = "messageTrafficMonitorTask", lockAtMostFor = 18000, lockAtLeastFor = 18000)

    public void clusterWarnStatistics() {
        log.info("======================clusterWarnStatistics-->start======================================");
        log.info("current time :{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.YMD_DASH_BLANK_HMS_COLON)));
        warnInfoService.clusterWarnStatistics();
        log.info("======================clusterWarnStatistics-->end======================================");
    }
}
