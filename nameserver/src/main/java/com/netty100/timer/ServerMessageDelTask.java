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
 * @Description 移除10天前的服务器消息数据
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServerMessageDelTask {
    public final ServerTrafficService serverTrafficService;

    @Scheduled(cron = "0 40 0 * * ? ")
    @SchedulerLock(name = "serverMessageDelTask", lockAtMostFor = 18000, lockAtLeastFor = 18000)
    public void serverMessageDel() {
        log.info("======================serverMessageRemove-->start======================================");
        log.info("serverMessageRemove ==>current time :{}",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.YMD_DASH_BLANK_HMS_COLON)));
        serverTrafficService.serverMessageRemove();
        log.info("======================serverMessageRemove-->end======================================");
    }
}
