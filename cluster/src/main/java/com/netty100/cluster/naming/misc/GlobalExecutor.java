/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netty100.cluster.naming.misc;

import com.netty100.cluster.common.executor.ExecutorFactory;
import com.netty100.cluster.common.executor.NameThreadFactory;
import com.netty100.cluster.core.utils.ClassUtils;
import com.netty100.cluster.sys.env.EnvUtil;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Global executor for naming.
 *
 * @author yewenhai
 */
@SuppressWarnings({"checkstyle:indentation", "PMD.ThreadPoolCreationRule"})
public class GlobalExecutor {
    
    private static final long SERVER_STATUS_UPDATE_PERIOD = TimeUnit.SECONDS.toMillis(5);
    
    public static final int DEFAULT_THREAD_COUNT = EnvUtil.getAvailableProcessors(0.5);
    
    private static final ScheduledExecutorService NAMING_TIMER_EXECUTOR = ExecutorFactory.Managed
            .newScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
                    EnvUtil.getAvailableProcessors(2), new NameThreadFactory("com.netty100.cluster.naming.timer"));
    
    private static final ExecutorService MYSQL_CHECK_EXECUTOR = ExecutorFactory.Managed
            .newFixedExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class), DEFAULT_THREAD_COUNT,
                    new NameThreadFactory("com.netty100.cluster.naming.mysql.checker"));
    
    private static final ScheduledExecutorService TCP_SUPER_SENSE_EXECUTOR = ExecutorFactory.Managed
            .newScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class), DEFAULT_THREAD_COUNT,
                    new NameThreadFactory("com.netty100.cluster.naming.supersense.checker"));
    
    private static final ExecutorService TCP_CHECK_EXECUTOR = ExecutorFactory.Managed
            .newFixedExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class), 2,
                    new NameThreadFactory("com.netty100.cluster.naming.tcp.check.worker"));
    
    private static final ScheduledExecutorService NAMING_HEALTH_EXECUTOR = ExecutorFactory.Managed
            .newScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
                    Integer.max(Integer.getInteger("com.netty100.cluster.naming.health.thread.num", DEFAULT_THREAD_COUNT),
                            1), new NameThreadFactory("com.netty100.cluster.naming.health"));
    
    private static final ScheduledExecutorService RETRANSMITTER_EXECUTOR = ExecutorFactory.Managed
            .newSingleScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
                    new NameThreadFactory("com.netty100.cluster.naming.push.retransmitter"));
    
    private static final ScheduledExecutorService UDP_SENDER_EXECUTOR = ExecutorFactory.Managed
            .newSingleScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
                    new NameThreadFactory("com.netty100.cluster.naming.push.udpSender"));
    
    private static final ScheduledExecutorService SERVER_PERFORMANCE_EXECUTOR = ExecutorFactory.Managed
            .newSingleScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
                    new NameThreadFactory("com.netty100.cluster.naming.cap-server-performance"));
    
    private static final ScheduledExecutorService EXPIRED_CLIENT_CLEANER_EXECUTOR = ExecutorFactory.Managed
            .newSingleScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
                    new NameThreadFactory("com.netty100.cluster.naming.remote-connection-manager"));
    
    private static final ExecutorService PUSH_CALLBACK_EXECUTOR = ExecutorFactory.Managed
            .newSingleExecutorService("Push", new NameThreadFactory("com.netty100.cluster.naming.push.callback"));
    
    private static final ScheduledExecutorService MONITOR_HEALTH_CHECK_POOL_EXECUTOR = ExecutorFactory.Managed
            .newScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
                            1, new NameThreadFactory("com.netty100.cluster.naming.health-check-pool"));
    
    public static void registerServerStatusUpdater(Runnable runnable) {
        NAMING_TIMER_EXECUTOR.scheduleAtFixedRate(runnable, 0, SERVER_STATUS_UPDATE_PERIOD, TimeUnit.MILLISECONDS);
    }
    
    public static void executeMysqlCheckTask(Runnable runnable) {
        MYSQL_CHECK_EXECUTOR.execute(runnable);
    }
    
    public static void submitTcpCheck(Runnable runnable) {
        TCP_CHECK_EXECUTOR.submit(runnable);
    }
    
    public static <T> List<Future<T>> invokeAllTcpSuperSenseTask(Collection<? extends Callable<T>> tasks)
            throws InterruptedException {
        return TCP_SUPER_SENSE_EXECUTOR.invokeAll(tasks);
    }
    
    public static void executeTcpSuperSense(Runnable runnable) {
        TCP_SUPER_SENSE_EXECUTOR.execute(runnable);
    }
    
    public static void scheduleTcpSuperSenseTask(Runnable runnable, long delay, TimeUnit unit) {
        TCP_SUPER_SENSE_EXECUTOR.schedule(runnable, delay, unit);
    }
    
    public static ScheduledFuture<?> scheduleNamingHealth(Runnable command, long delay, TimeUnit unit) {
        return NAMING_HEALTH_EXECUTOR.schedule(command, delay, unit);
    }
    
    public static ScheduledFuture<?> scheduleNamingHealth(Runnable command, long initialDelay, long delay,
            TimeUnit unit) {
        return NAMING_HEALTH_EXECUTOR.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
    
    public static void scheduleRetransmitter(Runnable runnable, long delay, TimeUnit unit) {
        RETRANSMITTER_EXECUTOR.schedule(runnable, delay, unit);
    }
    
    public static ScheduledFuture<?> scheduleUdpSender(Runnable runnable, long delay, TimeUnit unit) {
        return UDP_SENDER_EXECUTOR.schedule(runnable, delay, unit);
    }
    
    public static void scheduleUdpReceiver(Runnable runnable) {
        NAMING_TIMER_EXECUTOR.submit(runnable);
    }
    
    public static void schedulePerformanceLogger(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        SERVER_PERFORMANCE_EXECUTOR.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }
    
    public static void scheduleExpiredClientCleaner(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        EXPIRED_CLIENT_CLEANER_EXECUTOR.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }
    
    public static ExecutorService getCallbackExecutor() {
        return PUSH_CALLBACK_EXECUTOR;
    }

    public static ScheduledFuture<?> scheduleMonitorHealthCheckPool(Runnable runnable, long initialDelay, long delay,
            TimeUnit unit) {
        return MONITOR_HEALTH_CHECK_POOL_EXECUTOR.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }
}
