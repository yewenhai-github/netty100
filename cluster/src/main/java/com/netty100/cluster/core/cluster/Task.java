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

package com.netty100.cluster.core.cluster;

import com.netty100.cluster.common.utils.ExceptionUtil;
import com.netty100.cluster.core.utils.Loggers;

/**
 * task.
 *
 * @author yewenhai
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class Task implements Runnable {
    
    protected volatile boolean shutdown = false;
    
    @Override
    public void run() {
        if (shutdown) {
            return;
        }
        try {
            executeBody();
        } catch (Throwable t) {
            Loggers.CORE.error("this task execute has error : {}", ExceptionUtil.getStackTrace(t));
        } finally {
            if (!shutdown) {
                after();
            }
        }
    }
    
    /**
     * Task executive.
     */
    protected abstract void executeBody();
    
    /**
     * after executeBody should do.
     */
    protected void after() {
    
    }
    
    public void shutdown() {
        shutdown = true;
    }
    
}
