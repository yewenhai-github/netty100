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

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.common.lifecycle.Closeable;
import com.netty100.cluster.common.task.AbstractExecuteTask;
import com.netty100.cluster.common.task.CapTask;
import com.netty100.cluster.common.task.CapTaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cap execute task execute worker.
 *
 * @author yewenhai
 */
public final class TaskExecuteWorker implements CapTaskProcessor, Closeable {
    
    /**
     * Max task queue size 32768.
     */
    private static final int QUEUE_CAPACITY = 1 << 15;
    
    private final Logger log;
    
    private final String name;
    
    private final BlockingQueue<Runnable> queue;
    
    private final AtomicBoolean closed;
    
    private final InnerWorker realWorker;
    
    public TaskExecuteWorker(final String name, final int mod, final int total) {
        this(name, mod, total, null);
    }
    
    public TaskExecuteWorker(final String name, final int mod, final int total, final Logger logger) {
        this.name = name + "_" + mod + "%" + total;
        this.queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.closed = new AtomicBoolean(false);
        this.log = null == logger ? LoggerFactory.getLogger(TaskExecuteWorker.class) : logger;
        realWorker = new InnerWorker(this.name);
        realWorker.start();
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public boolean process(CapTask task) {
        if (task instanceof AbstractExecuteTask) {
            putTask((Runnable) task);
        }
        return true;
    }
    
    private void putTask(Runnable task) {
        try {
            queue.put(task);
        } catch (InterruptedException ire) {
            log.error(ire.toString(), ire);
        }
    }
    
    public int pendingTaskCount() {
        return queue.size();
    }
    
    /**
     * Worker status.
     */
    public String status() {
        return name + ", pending tasks: " + pendingTaskCount();
    }
    
    @Override
    public void shutdown() throws CapException {
        queue.clear();
        closed.compareAndSet(false, true);
        realWorker.interrupt();
    }
    
    /**
     * Inner execute worker.
     */
    private class InnerWorker extends Thread {
        
        InnerWorker(String name) {
            setDaemon(false);
            setName(name);
        }
        
        @Override
        public void run() {
            while (!closed.get()) {
                try {
                    Runnable task = queue.take();
                    long begin = System.currentTimeMillis();
                    task.run();
                    long duration = System.currentTimeMillis() - begin;
                    if (duration > 1000L) {
                        log.warn("task {} takes {}ms", task, duration);
                    }
                } catch (Throwable e) {
                    log.error("[TASK-FAILED] " + e, e);
                }
            }
        }
    }
}
