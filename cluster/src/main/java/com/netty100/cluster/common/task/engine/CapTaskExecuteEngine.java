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

import com.netty100.cluster.common.lifecycle.Closeable;
import com.netty100.cluster.common.task.CapTask;
import com.netty100.cluster.common.task.CapTaskProcessor;

import java.util.Collection;

/**
 * Cap task execute engine.
 *
 * @author why
 */
public interface CapTaskExecuteEngine<T extends CapTask> extends Closeable {
    
    /**
     * Get Task size in execute engine.
     *
     * @return size of task
     */
    int size();
    
    /**
     * Whether the execute engine is empty.
     *
     * @return true if the execute engine has no task to do, otherwise false
     */
    boolean isEmpty();
    
    /**
     * Add task processor {@link CapTaskProcessor} for execute engine.
     *
     * @param key           key of task
     * @param taskProcessor task processor
     */
    void addProcessor(Object key, CapTaskProcessor taskProcessor);
    
    /**
     * Remove task processor {@link CapTaskProcessor} form execute engine for key.
     *
     * @param key key of task
     */
    void removeProcessor(Object key);
    
    /**
     * Try to get {@link CapTaskProcessor} by key, if non-exist, will return default processor.
     *
     * @param key key of task
     * @return task processor for task key or default processor if task processor for task key non-exist
     */
    CapTaskProcessor getProcessor(Object key);
    
    /**
     * Get all processor key.
     *
     * @return collection of processors
     */
    Collection<Object> getAllProcessorKey();
    
    /**
     * Set default task processor. If do not find task processor by task key, use this default processor to process
     * task.
     *
     * @param defaultTaskProcessor default task processor
     */
    void setDefaultTaskProcessor(CapTaskProcessor defaultTaskProcessor);
    
    /**
     * Add task into execute pool.
     *
     * @param key  key of task
     * @param task task
     */
    void addTask(Object key, T task);
    
    /**
     * Remove task.
     *
     * @param key key of task
     * @return cap task
     */
    T removeTask(Object key);
    
    /**
     * Get all task keys.
     *
     * @return collection of task keys.
     */
    Collection<Object> getAllTaskKeys();
}
