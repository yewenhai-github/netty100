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

import com.netty100.cluster.common.task.AbstractDelayTask;
import com.netty100.cluster.consistency.DataOperation;
import com.netty100.cluster.core.distributed.distro.entity.DistroKey;
import com.netty100.cluster.common.task.engine.CapDelayTaskExecuteEngine;

/**
 * Distro delay task.
 *
 * @author why
 */
public class DistroDelayTask extends AbstractDelayTask {
    // 当前任务处理数据的key
    private final DistroKey distroKey;
    // 当前任务处理数据的操作类型
    private DataOperation action;
    // 当前任务创建的时间
    private long createTime;
    
    public DistroDelayTask(DistroKey distroKey, long delayTime) {
        this(distroKey, DataOperation.CHANGE, delayTime);
    }
    // 构造一个延迟任务
    public DistroDelayTask(DistroKey distroKey, DataOperation action, long delayTime) {
        this.distroKey = distroKey;
        this.action = action;
        this.createTime = System.currentTimeMillis();
        // 创建时设置上次处理的时间
        setLastProcessTime(createTime);
        // 设置间隔多久执行
        setTaskInterval(delayTime);
    }
    
    public DistroKey getDistroKey() {
        return distroKey;
    }
    
    public DataOperation getAction() {
        return action;
    }
    
    public long getCreateTime() {
        return createTime;
    }

    /**
     * 从字面意思是合并任务，实际的操作证明它是用于更新过时的任务
     * 在向任务列表添加新的任务时，使用新任务的key来从任务列表获取，若结果不为空，表明此任务已经存在
     * 相同的任务再次添加的话，就重复了，因此再此合并
     * 为什么新的任务会过时？（新任务指的是当前类）
     * 想要理解此处逻辑，请参考{@link CapDelayTaskExecuteEngine#addTask(Object,
     *  AbstractDelayTask)}.添加任务时是带锁操作的。因此添加的先后顺序不能保证
     * @param task task 已存在的任务
     */
    @Override
    public void merge(AbstractDelayTask task) {
        if (!(task instanceof DistroDelayTask)) {
            return;
        }
        DistroDelayTask oldTask = (DistroDelayTask) task;
        // 若旧的任务和新的任务的操作类型不同，并且新任务的创建时间小于旧任务的创建时间，说明当前这个新任务还未被添加成功
        // 这个新的任务已经过时了，不需要再执行这个任务的操作，因此将旧的任务的操作类型和创建时间设置给新任务
        if (!action.equals(oldTask.getAction()) && createTime < oldTask.getCreateTime()) {
            action = oldTask.getAction();
            createTime = oldTask.getCreateTime();
        }
        setLastProcessTime(oldTask.getLastProcessTime());
    }
}
