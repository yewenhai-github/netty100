package com.netty100.timer;

import cn.hutool.core.collection.CollectionUtil;
import com.netty100.entity.WarnConfig;
import com.netty100.service.WarnConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报警配置定时刷新
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WarnConfigFreshTask {
    private final WarnConfigService warnConfigService;

    @Scheduled(cron = "30 * * * * ?")
    @SchedulerLock(name = "warnConfigFreshTask", lockAtMostFor = 2000, lockAtLeastFor = 2000)
    public void refreshAlarmConfig() {
        long start = System.currentTimeMillis();
        List<WarnConfig> warnConfigs = warnConfigService.queryAll();
        if (CollectionUtil.isEmpty(warnConfigs)) {
            log.info("warn config is null");
            return;
        }
        warnConfigService.setConfigTable(warnConfigs);
        log.info("refresh alarm config use: {}ms", (System.currentTimeMillis() - start));
    }
}
