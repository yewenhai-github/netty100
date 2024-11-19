package com.netty100.timer;

import cn.hutool.core.date.TimeInterval;
import com.netty100.service.ClientConnectionService;
import com.netty100.service.ServerConnectionService;
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
public class InvalidConnectionDelTask {

    private final ClientConnectionService clientConnectionService;

    private final ServerConnectionService serverConnectionService;

    /**
     * 根据心跳报文信息,删除游戏端连接表和服务器端连接表中的
     * 心跳超时连接
     */
    @Scheduled(cron = "0 */1 * * * ?")
    @SchedulerLock(name = "deleteInvalidConnection", lockAtMostFor = 18000, lockAtLeastFor = 18000)
    public void deleteInvalidConnection() {
        val timer = new TimeInterval(false);
        timer.start();
        val timeLimit = DateUtils.addMinutes(new Date(), -5);
        int count1 = clientConnectionService.deleteInvalidConnections(timeLimit);
        int count2 = serverConnectionService.deleteInvalidConnections(timeLimit);
        log.info("删除心跳超时游戏端连接数:{},服务器端连接数:{},耗时:{}毫秒", count1, count2, timer.intervalMs());
    }
}
