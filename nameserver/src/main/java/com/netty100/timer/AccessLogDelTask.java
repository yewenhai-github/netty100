package com.netty100.timer;

import cn.hutool.core.date.TimeInterval;
import com.netty100.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author why
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccessLogDelTask {

    private final AccessLogService accessLogService;

    @Scheduled(cron = "0 0 0 */1 * ?")
    @SchedulerLock(name = "deleteInvalidAccessLog", lockAtMostFor = 18000, lockAtLeastFor = 18000)
    public void deleteInvalidAccessLog() {
        val timeInterval = new TimeInterval(false);
        timeInterval.start();
        val timeLimit = DateUtils.addDays(new Date(), -10);
        accessLogService.deleteInvalidAccessLog(timeLimit);
        log.info("清除访问历史数据成功,耗时:{}毫秒", timeInterval.intervalMs());
    }
}
