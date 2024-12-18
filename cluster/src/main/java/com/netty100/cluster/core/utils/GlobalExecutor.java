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

package com.netty100.cluster.core.utils;

import com.netty100.cluster.common.executor.ExecutorFactory;
import com.netty100.cluster.common.executor.NameThreadFactory;
import com.netty100.cluster.common.utils.ThreadFactoryBuilder;
import com.netty100.cluster.sys.env.EnvUtil;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * core module global executor.
 *
 * @author why
 */
@SuppressWarnings("all")
public class GlobalExecutor {
    
    private static final ScheduledExecutorService COMMON_EXECUTOR = ExecutorFactory.Managed
            .newScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class), 4,
                    new NameThreadFactory("com.netty100.cluster.core.common"));
    
    private static final ScheduledExecutorService DISTRO_EXECUTOR = ExecutorFactory.Managed
            .newScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
                    EnvUtil.getAvailableProcessors(2), new NameThreadFactory("com.netty100.cluster.core.protocal.distro"));
    
    public static final ThreadPoolExecutor sdkRpcExecutor = new ThreadPoolExecutor(
            EnvUtil.getAvailableProcessors(RemoteUtils.getRemoteExecutorTimesOfProcessors()),
            EnvUtil.getAvailableProcessors(RemoteUtils.getRemoteExecutorTimesOfProcessors()), 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(RemoteUtils.getRemoteExecutorQueueSize()),
            new ThreadFactoryBuilder().daemon(true).nameFormat("cap-grpc-executor-%d").build());
    
    public static final ThreadPoolExecutor clusterRpcExecutor = new ThreadPoolExecutor(
            EnvUtil.getAvailableProcessors(RemoteUtils.getRemoteExecutorTimesOfProcessors()),
            EnvUtil.getAvailableProcessors(RemoteUtils.getRemoteExecutorTimesOfProcessors()), 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(RemoteUtils.getRemoteExecutorQueueSize()),
            new ThreadFactoryBuilder().daemon(true).nameFormat("cap-cluster-grpc-executor-%d").build());
    
    public static void runWithoutThread(Runnable runnable) {
        runnable.run();
    }
    
    public static void executeByCommon(Runnable runnable) {
        if (COMMON_EXECUTOR.isShutdown()) {
            return;
        }
        COMMON_EXECUTOR.execute(runnable);
    }
    
    public static void scheduleByCommon(Runnable runnable, long delayMs) {
        if (COMMON_EXECUTOR.isShutdown()) {
            return;
        }
        COMMON_EXECUTOR.schedule(runnable, delayMs, TimeUnit.MILLISECONDS);
    }
    
    public static void submitLoadDataTask(Runnable runnable) {
        DISTRO_EXECUTOR.submit(runnable);
    }
    
    public static void submitLoadDataTask(Runnable runnable, long delay) {
        DISTRO_EXECUTOR.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }
    
    public static void schedulePartitionDataTimedSync(Runnable runnable, long interval) {
        DISTRO_EXECUTOR.scheduleWithFixedDelay(runnable, interval, interval, TimeUnit.MILLISECONDS);
    }
    
}
