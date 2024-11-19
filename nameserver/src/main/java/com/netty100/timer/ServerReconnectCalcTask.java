package com.netty100.timer;

import com.netty100.service.ReportMinuteDataService;
import com.netty100.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description 重连定时计算
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/15
 */
@Component
@Slf4j
public class ServerReconnectCalcTask {
    @Autowired
    private ReportMinuteDataService reportMinuteDataService;

    @Scheduled(cron = "55 */1 * * * ? ")
    @SchedulerLock(name = "serverReconnectCalcTask", lockAtMostFor = 18000, lockAtLeastFor = 18000)
    public void serverReconnectCalc() {
        String formatNow = DateUtils.getFormatNow(DateUtils.YMD_DASH_BLANK_HM_START_COLON);
        String fiveMinuteAgoTime = DateUtils.formatYMDPlusMinute(-6, DateUtils.YMD_DASH_BLANK_HM_START_COLON);
        String oneMinuteAgoTime = DateUtils.formatYMDPlusMinute(-1, DateUtils.YMD_DASH_BLANK_HM_START_COLON);
        log.info("==========serverReconnectCalc-->start==formatNow:{}==fiveMinuteAgoTime:{}==oneMinuteAgoTime:{}===", formatNow, fiveMinuteAgoTime, oneMinuteAgoTime);
        log.info("current time :{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.YMD_DASH_BLANK_HMS_COLON)));
        reportMinuteDataService.serverReconnectCalc(formatNow, fiveMinuteAgoTime, oneMinuteAgoTime);
        log.info("======================serverReconnectCalc-->end======================================");
    }
}
