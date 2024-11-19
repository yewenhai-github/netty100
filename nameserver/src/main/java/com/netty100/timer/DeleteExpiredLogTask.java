package com.netty100.timer;

import cn.hutool.core.map.MapUtil;
import com.netty100.service.ClientChannelLogService;
import com.netty100.service.KernelMessageLogService;
import com.netty100.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author why
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteExpiredLogTask {

    private final ClientChannelLogService clientChannelLogService;

    private final KernelMessageLogService kernelMessageLogService;

    private final MessageService messageService;

    @Scheduled(cron = "0 0 4 * * ?")
    @SchedulerLock(name = "deleteExpiredLogTask", lockAtMostFor = 60000, lockAtLeastFor = 60000)
    public void deleteExpiredLogTask() {
        Date date = DateUtils.addDays(new Date(), -2);

        deleteChannelLog(date);
        deleteKernelLog(date);
        deleteMessageLog(date);
    }

    private void deleteChannelLog(Date date) {
        log.info("deleteChannelLog 开始");
        Map<String, Long> channelMap = clientChannelLogService.queryMaxMinId(date);
        if (!CollectionUtils.isEmpty(channelMap)) {
            Long maxId = MapUtil.getLong(channelMap, "maxId", 0L);
            Long minId = MapUtil.getLong(channelMap, "minId", 0L);
            if (maxId == 0L) {
                log.info("deleteChannelLog;MaxId:{},MinID:{},查询出来的数据为空", maxId, minId);
                return;
            }
            long diff = maxId - minId;
            long batch = 20_0000L;
            if (diff <= batch) {
                clientChannelLogService.deleteExpiredLog(date);
                log.info("deleteChannelLog;MaxId:{},MinID:{},查询出来的数据小于{},直接全部删除", maxId, minId, batch);
                return;
            }

            long tmpIdStart = minId;

            for (int i = 0; i < 500; i++) {
                long tmpIdEnd = Math.min(tmpIdStart + batch, maxId);
                clientChannelLogService.deleteExpiredLogById(tmpIdEnd);
                log.info("deleteChannelLog,当前是第[{}]次循环;MaxId:{},MinID:{},本次minId:{},maxId:{}", i, maxId, minId, tmpIdStart, tmpIdEnd);
                tmpIdStart += batch;
                if (tmpIdEnd == maxId) {
                    return;
                }
            }
        }
        log.info("deleteChannelLog 结束");
    }

    private void deleteKernelLog(Date date) {
        log.info("deleteKernelLog 开始");
        Map<String, Long> channelMap = kernelMessageLogService.queryMaxMinId(date);
        if (!CollectionUtils.isEmpty(channelMap)) {
            Long maxId = MapUtil.getLong(channelMap, "maxId", 0L);
            Long minId = MapUtil.getLong(channelMap, "minId", 0L);
            if (maxId == 0L) {
                log.info("deleteKernelLog;MaxId:{},MinID:{},查询出来的数据为空", maxId, minId);
                return;
            }
            long diff = maxId - minId;
            long batch = 20_0000L;
            if (diff <= batch) {
                kernelMessageLogService.deleteExpiredLog(date);
                log.info("deleteKernelLog;MaxId:{},MinID:{},查询出来的数据小于{},直接全部删除", maxId, minId, batch);
                return;
            }

            long tmpIdStart = minId;

            for (int i = 0; i < 500; i++) {
                long tmpIdEnd = Math.min(tmpIdStart + batch, maxId);
                kernelMessageLogService.deleteExpiredLogById(tmpIdEnd);
                log.info("deleteKernelLog,当前是第[{}]次循环;MaxId:{},MinID:{},本次minId:{},maxId:{}", i, maxId, minId, tmpIdStart, tmpIdEnd);
                tmpIdStart += batch;
                if (tmpIdEnd == maxId) {
                    return;
                }
            }
        }
        log.info("deleteKernelLog 结束");
    }

    private void deleteMessageLog(Date date) {
        log.info("deleteMessageLog 开始");
        Map<String, Long> channelMap = messageService.queryMaxMinId(date);
        if (!CollectionUtils.isEmpty(channelMap)) {
            Long maxId = MapUtil.getLong(channelMap, "maxId", 0L);
            Long minId = MapUtil.getLong(channelMap, "minId", 0L);
            if (maxId == 0L) {
                log.info("deleteMessageLog;MaxId:{},MinID:{},查询出来的数据为空", maxId, minId);
                return;
            }
            long diff = maxId - minId;
            long batch = 20_0000L;
            if (diff <= batch) {
                messageService.deleteExpiredLog(date);
                log.info("deleteMessageLog;MaxId:{},MinID:{},查询出来的数据小于{},直接全部删除", maxId, minId, batch);
                return;
            }

            long tmpIdStart = minId;

            for (int i = 0; i < 500; i++) {
                long tmpIdEnd = Math.min(tmpIdStart + batch, maxId);
                messageService.deleteExpiredLogById(tmpIdEnd);
                log.info("deleteMessageLog,当前是第[{}]次循环;MaxId:{},MinID:{},本次minId:{},maxId:{}", i, maxId, minId, tmpIdStart, tmpIdEnd);
                tmpIdStart += batch;
                if (tmpIdEnd == maxId) {
                    return;
                }
            }
        }
        log.info("deleteMessageLog 结束");
    }
}
