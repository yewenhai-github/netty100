package com.netty100.kernel.utils;

import cn.hutool.core.thread.NamedThreadFactory;
import com.netty100.kernel.devops.utils.WhyCloudUtils;
import com.netty100.common.utils.SysUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/6/7
 * @since 1.0.0, 2022/6/7
 */
@Slf4j
@Component
public class KernelConsole {
    @Autowired
    private WhyCloudUtils whyCloudUtils;

    @PostConstruct
    public void doCommand() {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("kernel-c-", true));
        executorService.scheduleAtFixedRate(() -> {
            if(0 == WhyChannelUtils.getChannelCount() && 0 == WhyChannelUtils.getClientUserCount() && 0 == WhyChannelUtils.getServerUserCount()){
                return;
            }

            log.warn("内核内存配置监控：{},集群地址{},总管道数:{},客户端管道数：{},服务端管道数：{}",
                    SysUtility.getHostIp(),
                    whyCloudUtils.getAllServiceUrls(),
                    WhyChannelUtils.getChannelCount(),
                    WhyChannelUtils.getClientUserCount(),
                    WhyChannelUtils.getServerUserCount()
            );
        },0, 3600, TimeUnit.SECONDS);
    }
}
