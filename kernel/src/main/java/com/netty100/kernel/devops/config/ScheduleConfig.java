package com.netty100.kernel.devops.config;

import cn.hutool.core.thread.NamedThreadFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * 基于 @Scheduled 的定时任务，其实会在 bean 实例化阶段 的  BeanPostProcessor（的具体子类实现ScheduledAnnotationBeanPostProcessor  的 postProcessAfterInitialization）将
 * 所有附带  @Scheduled注解的方法检测出，分析对应的 参数内容， 然后加入各个任务队列之中。
 * 我们配置了 定时任务 使用自己的 ScheduledThreadPoolExecutor  内部其实 基于 DelayQueue，每次任务执行完成之后会计算是否需要下次执行，以及下次执行的时间，然后将任务在放入队列之中。
 * 关于 @EnableScheduling，不加这个注解，在项目启动时  @Scheduled(cron = "0 30 19 * * ?") 这个不会执行， 但是 @Scheduled(fixedDelay = 2)会执行， 因为
 * initialDelay是默认值的缘故，在将任务加入队列之前会 先 调用一下当前的任务，所以项目启动时 会执行一次。
 *
 * @author yewenhai
 * @version 1.0.0, 2022/4/12
 * @since 1.0.0, 2022/4/12
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(
                new ScheduledThreadPoolExecutor(
                        Runtime.getRuntime().availableProcessors(), new NamedThreadFactory("kernel-schedule-", false)
                ));
    }
}
