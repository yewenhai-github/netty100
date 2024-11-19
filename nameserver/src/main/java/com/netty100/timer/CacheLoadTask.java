package com.netty100.timer;

import com.netty100.utils.RedisTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author why
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CacheLoadTask {

    private final RedisTool redisTool;

    @Scheduled(initialDelay = -1, fixedRate = 1500)
    @SchedulerLock(name = "cacheLoadTask2", lockAtMostFor = 2000, lockAtLeastFor = 1000)
    public void cacheLoadTask() {
        redisTool.cacheData();
    }
}
