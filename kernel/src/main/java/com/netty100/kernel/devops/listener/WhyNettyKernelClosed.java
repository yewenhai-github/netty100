package com.netty100.kernel.devops.listener;

import com.netty100.kernel.autoconfig.WhyKernelProperties;
import com.netty100.kernel.devops.queue.WhyMessageQueue;
import com.netty100.kernel.devops.timer.*;
import com.netty100.kernel.devops.utils.WheelTimerUtils;
import com.netty100.kernel.protocol.LogPointCode;
import com.netty100.common.utils.SysUtility;
import io.netty.util.Timeout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/5/19
 * @since 1.0.0, 2022/5/19
 */
@Slf4j
@Component
public class WhyNettyKernelClosed implements ApplicationListener<ContextClosedEvent> {
    @Autowired
    private WhyKernelProperties whyKernelProperties;
    @Autowired
    private PushClientChannelActive pushClientChannelActive;
    @Autowired
    private PushClientChannelInactive pushClientChannelInactive;
    @Autowired
    private PushClientChannelExceptionCaught pushClientChannelExceptionCaught;
    @Autowired
    private PushClientChannelIdleInactive pushClientChannelIdleInactive;
    @Autowired
    private PushServerChannelActive pushServerChannelActive;
    @Autowired
    private PushServerChannelInactive pushServerChannelInactive;
    @Autowired
    private PushServerChannelExceptionCaught pushServerChannelExceptionCaught;
    @Autowired
    private PushKernelNameServerExitRegister pushKernelNameServerExitRegister;

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        if(SysUtility.isEmpty(whyKernelProperties.getDomain())){
            return;
        }

        log.info("内核节点{}程序停止中...", SysUtility.getHostIp());
        try {
            //上报当前服务器宕机内核日志
            WhyMessageQueue.pushKernelMessageLogQueue(null, LogPointCode.K01.getCode(), LogPointCode.K01.getMessage());
            //上报当前服务器宕机信息
            pushKernelNameServerExitRegister.doCommand();

            //触发还未执行的定时程序，将剩余数据上传
            pushClientChannelActive.registerClientChannelActiveQueue();
            pushClientChannelInactive.registerClientChannelInactiveQueue();
            pushClientChannelExceptionCaught.registerClientChannelExceptionCaughtQueue();
            pushClientChannelIdleInactive.registerClientChannelIdleInactiveQueue();
            pushServerChannelActive.registerServerChannelActiveQueue();
            pushServerChannelInactive.registerServerChannelInactiveQueue();
            pushServerChannelExceptionCaught.registerServerChannelExceptionCaughtQueue();

            //触发还未放入时间轮槽子收集的数据
            PushClientMessageLog.addClientMessageLogTask(whyKernelProperties, true);
            PushKernelMessageLog.addKernelMessageLogTask(whyKernelProperties, true);
            PushClientChannelIdelActive.addClientChannelIdleActiveTask(whyKernelProperties, true);
            PushServerChannelIdelActive.addServerChannelIdleActiveTask(whyKernelProperties, true);
            PushClientChannelLog.addClientChannelLogTask(whyKernelProperties, true);
            //停止后抓取未处理的任务，执行
            Set<Timeout> unprocessedTimeouts = WheelTimerUtils.hashedWheel.stop();
            if(unprocessedTimeouts.size() > 0){
                log.info("内核节点{}时间轮任务（{}个）处理中...", SysUtility.getHostIp(), unprocessedTimeouts.size());
                AtomicInteger index = new AtomicInteger();
                unprocessedTimeouts.stream().forEach(timeout -> {
                    try {
                        timeout.task().run(timeout);
                        index.incrementAndGet();
                    } catch (Exception e) {
                        log.error("内核节点{}时间轮任务处理失败，当前第（{}）个任务...", SysUtility.getHostIp(), index, e);
                    }
                });
                log.info("内核节点{}时间轮任务（{}个）处理完成...", SysUtility.getHostIp(), index);
            }
            log.info("内核节点{}程序停止完成，临时数据已上传完成...", SysUtility.getHostIp());
        } catch (Exception e) {
            log.error("内核节点{}程序停止失败", SysUtility.getHostIp(), e);
        }
    }

}