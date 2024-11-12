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

package com.netty100.cluster.core.distributed.distro.task.delay;

import com.netty100.cluster.common.task.CapTask;
import com.netty100.cluster.common.task.CapTaskProcessor;
import com.netty100.cluster.core.distributed.distro.component.DistroComponentHolder;
import com.netty100.cluster.core.distributed.distro.entity.DistroKey;
import com.netty100.cluster.core.distributed.distro.task.execute.DistroSyncChangeTask;
import com.netty100.cluster.core.distributed.distro.task.execute.DistroSyncDeleteTask;
import com.netty100.cluster.core.distributed.distro.task.DistroTaskEngineHolder;

/**
 * Distro delay task processor.
 *
 * @author yewenhai
 */
public class DistroDelayTaskProcessor implements CapTaskProcessor {
    
    private final DistroTaskEngineHolder distroTaskEngineHolder;
    
    private final DistroComponentHolder distroComponentHolder;
    
    public DistroDelayTaskProcessor(DistroTaskEngineHolder distroTaskEngineHolder,
            DistroComponentHolder distroComponentHolder) {
        this.distroTaskEngineHolder = distroTaskEngineHolder;
        this.distroComponentHolder = distroComponentHolder;
    }
    
    @Override
    public boolean process(CapTask task) {
        // 不处理非延迟任务
        if (!(task instanceof DistroDelayTask)) {
            return true;
        }
        DistroDelayTask distroDelayTask = (DistroDelayTask) task;
        DistroKey distroKey = distroDelayTask.getDistroKey();
        // 根据不同的操作类型创建具体的任务
        switch (distroDelayTask.getAction()) {
            case DELETE:
                DistroSyncDeleteTask syncDeleteTask = new DistroSyncDeleteTask(distroKey, distroComponentHolder);
                distroTaskEngineHolder.getExecuteWorkersManager().addTask(distroKey, syncDeleteTask);
                return true;
            case CHANGE:
            case ADD:
                //生成一个修改任务，调用DistroClientTransportAgent.syncData(DistroData data, String targetServer)
                DistroSyncChangeTask syncChangeTask = new DistroSyncChangeTask(distroKey, distroComponentHolder);
                distroTaskEngineHolder.getExecuteWorkersManager().addTask(distroKey, syncChangeTask);
                return true;
            default:
                return false;
        }
    }
}
