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

import com.netty100.cluster.common.task.AbstractExecuteTask;
import com.netty100.cluster.common.task.engine.CapExecuteTaskExecuteEngine;
import com.netty100.cluster.sys.env.EnvUtil;

/**
 * Naming execute task dispatcher.
 *
 * @author why
 */
public class NamingExecuteTaskDispatcher {
    
    private static final NamingExecuteTaskDispatcher INSTANCE = new NamingExecuteTaskDispatcher();
    
    private final CapExecuteTaskExecuteEngine executeEngine;
    
    private NamingExecuteTaskDispatcher() {
        executeEngine = new CapExecuteTaskExecuteEngine(EnvUtil.FUNCTION_MODE_NAMING, Loggers.SRV_LOG);
    }
    
    public static NamingExecuteTaskDispatcher getInstance() {
        return INSTANCE;
    }
    
    public void dispatchAndExecuteTask(Object dispatchTag, AbstractExecuteTask task) {
        executeEngine.addTask(dispatchTag, task);
    }
    
    public String workersStatus() {
        return executeEngine.workersStatus();
    }
    
    public void destroy() throws Exception {
        executeEngine.shutdown();
    }
}
