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

package com.netty100.cluster.common.task.engine;

import com.netty100.cluster.common.task.CapTask;
import com.netty100.cluster.common.task.CapTaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract cap task execute engine.
 *
 * @author yewenhai
 */
public abstract class AbstractCapTaskExecuteEngine<T extends CapTask> implements CapTaskExecuteEngine<T> {
    
    private final Logger log;
    
    private final ConcurrentHashMap<Object, CapTaskProcessor> taskProcessors = new ConcurrentHashMap<>();
    
    private CapTaskProcessor defaultTaskProcessor;
    
    public AbstractCapTaskExecuteEngine(Logger logger) {
        this.log = null != logger ? logger : LoggerFactory.getLogger(AbstractCapTaskExecuteEngine.class.getName());
    }
    
    @Override
    public void addProcessor(Object key, CapTaskProcessor taskProcessor) {
        taskProcessors.putIfAbsent(key, taskProcessor);
    }
    
    @Override
    public void removeProcessor(Object key) {
        taskProcessors.remove(key);
    }
    
    @Override
    public CapTaskProcessor getProcessor(Object key) {
        return taskProcessors.containsKey(key) ? taskProcessors.get(key) : defaultTaskProcessor;
    }
    
    @Override
    public Collection<Object> getAllProcessorKey() {
        return taskProcessors.keySet();
    }
    
    @Override
    public void setDefaultTaskProcessor(CapTaskProcessor defaultTaskProcessor) {
        this.defaultTaskProcessor = defaultTaskProcessor;
    }
    
    protected Logger getEngineLog() {
        return log;
    }
}
