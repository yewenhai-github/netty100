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

import com.netty100.cluster.common.task.CapTaskProcessor;
import com.netty100.cluster.common.task.engine.CapDelayTaskExecuteEngine;
import com.netty100.cluster.core.distributed.distro.entity.DistroKey;
import com.netty100.cluster.core.utils.Loggers;

/**
 * Distro delay task execute engine.
 *
 * @author yewenhai
 */
public class DistroDelayTaskExecuteEngine extends CapDelayTaskExecuteEngine {
    
    public DistroDelayTaskExecuteEngine() {
        super(DistroDelayTaskExecuteEngine.class.getName(), Loggers.DISTRO);
    }
    
    @Override
    public void addProcessor(Object key, CapTaskProcessor taskProcessor) {
        Object actualKey = getActualKey(key);
        super.addProcessor(actualKey, taskProcessor);
    }
    
    @Override
    public CapTaskProcessor getProcessor(Object key) {
        Object actualKey = getActualKey(key);
        return super.getProcessor(actualKey);
    }
    
    private Object getActualKey(Object key) {
        return key instanceof DistroKey ? ((DistroKey) key).getResourceType() : key;
    }
}
