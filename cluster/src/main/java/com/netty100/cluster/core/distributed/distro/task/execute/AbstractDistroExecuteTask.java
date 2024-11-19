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

package com.netty100.cluster.core.distributed.distro.task.execute;

import com.netty100.cluster.common.task.AbstractExecuteTask;
import com.netty100.cluster.consistency.DataOperation;
import com.netty100.cluster.core.distributed.distro.component.DistroCallback;
import com.netty100.cluster.core.distributed.distro.component.DistroComponentHolder;
import com.netty100.cluster.core.distributed.distro.component.DistroFailedTaskHandler;
import com.netty100.cluster.core.distributed.distro.component.DistroTransportAgent;
import com.netty100.cluster.core.distributed.distro.entity.DistroKey;
import com.netty100.cluster.core.distributed.distro.monitor.DistroRecord;
import com.netty100.cluster.core.distributed.distro.monitor.DistroRecordsHolder;
import com.netty100.cluster.core.utils.Loggers;

/**
 * Abstract distro execute task.
 *
 * @author why
 */
public abstract class AbstractDistroExecuteTask extends AbstractExecuteTask {
    
    private final DistroKey distroKey;
    
    private final DistroComponentHolder distroComponentHolder;
    
    protected AbstractDistroExecuteTask(DistroKey distroKey, DistroComponentHolder distroComponentHolder) {
        this.distroKey = distroKey;
        this.distroComponentHolder = distroComponentHolder;
    }
    
    protected DistroKey getDistroKey() {
        return distroKey;
    }
    
    protected DistroComponentHolder getDistroComponentHolder() {
        return distroComponentHolder;
    }
    
    @Override
    public void run() {
        // 获取被处理的数据资源类型
        String type = getDistroKey().getResourceType();
        // 根据类型获取数据传输代理
        DistroTransportAgent transportAgent = distroComponentHolder.findTransportAgent(type);
        if (null == transportAgent) {
            Loggers.DISTRO.warn("No found transport agent for type [{}]", type);
            return;
        }
        Loggers.DISTRO.info("[DISTRO-START] {}", toString());
        // 判断代理对象是否支持回调
        if (transportAgent.supportCallbackTransport()) {
            doExecuteWithCallback(new DistroExecuteCallback());
        } else {
            executeDistroTask();
        }
    }
    
    private void executeDistroTask() {
        try {
            boolean result = doExecute();
            if (!result) {
                // 执行失败之后，进行失败处理
                handleFailedTask();
            }
            Loggers.DISTRO.info("[DISTRO-END] {} result: {}", toString(), result);
        } catch (Exception e) {
            Loggers.DISTRO.warn("[DISTRO] Sync data change failed.", e);
            // 执行失败之后，进行失败处理
            handleFailedTask();
        }
    }
    
    /**
     * Get {@link DataOperation} for current task.
     *
     * @return data operation
     */
    protected abstract DataOperation getDataOperation();
    
    /**
     * Do execute for different sub class.
     *
     * @return result of execute
     */
    protected abstract boolean doExecute();
    
    /**
     * Do execute with callback for different sub class.
     *
     * @param callback callback
     */
    protected abstract void doExecuteWithCallback(DistroCallback callback);
    
    /**
     * Handle failed task.
     */
    protected void handleFailedTask() {
        String type = getDistroKey().getResourceType();
        // 使用失败任务处理器进行重试
        DistroFailedTaskHandler failedTaskHandler = distroComponentHolder.findFailedTaskHandler(type);
        if (null == failedTaskHandler) {
            Loggers.DISTRO.warn("[DISTRO] Can't find failed task for type {}, so discarded", type);
            return;
        }
        failedTaskHandler.retry(getDistroKey(), getDataOperation());
    }
    
    private class DistroExecuteCallback implements DistroCallback {
        
        @Override
        public void onSuccess() {
            DistroRecord distroRecord = DistroRecordsHolder.getInstance().getRecord(getDistroKey().getResourceType());
            distroRecord.syncSuccess();
            Loggers.DISTRO.info("[DISTRO-END] {} result: true", getDistroKey().toString());
        }
        
        @Override
        public void onFailed(Throwable throwable) {
            DistroRecord distroRecord = DistroRecordsHolder.getInstance().getRecord(getDistroKey().getResourceType());
            distroRecord.syncFail();
            if (null == throwable) {
                Loggers.DISTRO.info("[DISTRO-END] {} result: false", getDistroKey().toString());
            } else {
                Loggers.DISTRO.warn("[DISTRO] Sync data change failed. key: {}", getDistroKey().toString(), throwable);
            }
            handleFailedTask();
        }
    }
}
