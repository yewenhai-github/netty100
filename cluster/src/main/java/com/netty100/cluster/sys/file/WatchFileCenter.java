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

package com.netty100.cluster.sys.file;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.common.executor.ExecutorFactory;
import com.netty100.cluster.common.executor.NameThreadFactory;
import com.netty100.cluster.common.utils.ConcurrentHashSet;
import com.netty100.cluster.common.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unified file change monitoring management center, which uses {@link WatchService} internally. One file directory
 * corresponds to one {@link WatchService}. It can only monitor up to 32 file directories. When a file change occurs, a
 * {@link FileChangeEvent} will be issued
 *
 * @author yewenhai
 */
public class WatchFileCenter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WatchFileCenter.class);
    
    /**
     * Maximum number of monitored file directories.
     */
    private static final int MAX_WATCH_FILE_JOB = Integer.getInteger("cap.watch-file.max-dirs", 16);
    
    private static final Map<String, WatchDirJob> MANAGER = new HashMap<>(MAX_WATCH_FILE_JOB);
    
    private static final FileSystem FILE_SYSTEM = FileSystems.getDefault();
    
    private static final AtomicBoolean CLOSED = new AtomicBoolean(false);
    
    static {
        ThreadUtils.addShutdownHook(WatchFileCenter::shutdown);
    }
    
    /**
     * The number of directories that are currently monitored.
     */
    @SuppressWarnings("checkstyle:StaticVariableName")
    private static int NOW_WATCH_JOB_CNT = 0;
    
    /**
     * Register {@link FileWatcher} in this directory.
     *
     * @param paths   directory
     * @param watcher {@link FileWatcher}
     * @return register is success
     * @throws CapException NacosException
     */
    public static synchronized boolean registerWatcher(final String paths, FileWatcher watcher) throws CapException {
        checkState();
        if (NOW_WATCH_JOB_CNT == MAX_WATCH_FILE_JOB) {
            return false;
        }
        WatchDirJob job = MANAGER.get(paths);
        if (job == null) {
            job = new WatchDirJob(paths);
            job.start();
            MANAGER.put(paths, job);
            NOW_WATCH_JOB_CNT++;
        }
        job.addSubscribe(watcher);
        return true;
    }
    
    /**
     * Deregister all {@link FileWatcher} in this directory.
     *
     * @param path directory
     * @return deregister is success
     */
    public static synchronized boolean deregisterAllWatcher(final String path) {
        WatchDirJob job = MANAGER.get(path);
        if (job != null) {
            job.shutdown();
            MANAGER.remove(path);
            NOW_WATCH_JOB_CNT--;
            return true;
        }
        return false;
    }
    
    /**
     * close {@link WatchFileCenter}.
     */
    public static void shutdown() {
        if (!CLOSED.compareAndSet(false, true)) {
            return;
        }
        LOGGER.warn("[WatchFileCenter] start close");
        for (Map.Entry<String, WatchDirJob> entry : MANAGER.entrySet()) {
            LOGGER.warn("[WatchFileCenter] start to shutdown this watcher which is watch : " + entry.getKey());
            try {
                entry.getValue().shutdown();
            } catch (Throwable e) {
                LOGGER.error("[WatchFileCenter] shutdown has error : ", e);
            }
        }
        MANAGER.clear();
        NOW_WATCH_JOB_CNT = 0;
        LOGGER.warn("[WatchFileCenter] already closed");
    }
    
    /**
     * Deregister {@link FileWatcher} in this directory.
     *
     * @param path    directory
     * @param watcher {@link FileWatcher}
     * @return deregister is success
     */
    public static synchronized boolean deregisterWatcher(final String path, final FileWatcher watcher) {
        WatchDirJob job = MANAGER.get(path);
        if (job != null) {
            job.watchers.remove(watcher);
            return true;
        }
        return false;
    }
    
    private static class WatchDirJob extends Thread {
        
        private final ExecutorService callBackExecutor;
        
        private final String paths;
        
        private final WatchService watchService;
        
        private volatile boolean watch = true;
        
        private final Set<FileWatcher> watchers = new ConcurrentHashSet<>();
        
        public WatchDirJob(String paths) throws CapException {
            setName(paths);
            this.paths = paths;
            final Path p = Paths.get(paths);
            if (!p.toFile().isDirectory()) {
                throw new IllegalArgumentException("Must be a file directory : " + paths);
            }
            
            this.callBackExecutor = ExecutorFactory.newSingleExecutorService(
                    new NameThreadFactory("com.netty100.cluster.sys.file.watch-" + paths));
            
            try {
                WatchService service = FILE_SYSTEM.newWatchService();
                p.register(service, StandardWatchEventKinds.OVERFLOW, StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
                this.watchService = service;
            } catch (Throwable ex) {
                throw new CapException(CapException.SERVER_ERROR, ex);
            }
        }
        
        void addSubscribe(final FileWatcher watcher) {
            watchers.add(watcher);
        }
        
        void shutdown() {
            watch = false;
            
            //fix issue[https://github.com/alibaba/cap/issues/9393]
            try {
                watchService.close();
            } catch (IOException ignore) {
            }
            
            ThreadUtils.shutdownThreadPool(this.callBackExecutor);
        }
        
        @Override
        public void run() {
            while (watch && !this.isInterrupted()) {
                try {
                    final WatchKey watchKey = watchService.take();
                    final List<WatchEvent<?>> events = watchKey.pollEvents();
                    watchKey.reset();
                    if (callBackExecutor.isShutdown()) {
                        return;
                    }
                    if (events.isEmpty()) {
                        continue;
                    }
                    callBackExecutor.execute(() -> {
                        for (WatchEvent<?> event : events) {
                            WatchEvent.Kind<?> kind = event.kind();
                            
                            // Since the OS's event cache may be overflow, a backstop is needed
                            if (StandardWatchEventKinds.OVERFLOW.equals(kind)) {
                                eventOverflow();
                            } else {
                                eventProcess(event.context());
                            }
                        }
                    });
                } catch (InterruptedException | ClosedWatchServiceException ignore) {
                    Thread.interrupted();
                } catch (Throwable ex) {
                    LOGGER.error("An exception occurred during file listening : ", ex);
                }
            }
        }
        
        private void eventProcess(Object context) {
            final FileChangeEvent fileChangeEvent = FileChangeEvent.builder().paths(paths).context(context).build();
            final String str = String.valueOf(context);
            for (final FileWatcher watcher : watchers) {
                if (watcher.interest(str)) {
                    Runnable job = () -> watcher.onChange(fileChangeEvent);
                    Executor executor = watcher.executor();
                    if (executor == null) {
                        try {
                            job.run();
                        } catch (Throwable ex) {
                            LOGGER.error("File change event callback error : ", ex);
                        }
                    } else {
                        executor.execute(job);
                    }
                }
            }
        }
        
        private void eventOverflow() {
            File dir = Paths.get(paths).toFile();
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                // Subdirectories do not participate in listening
                if (file.isDirectory()) {
                    continue;
                }
                eventProcess(file.getName());
            }
        }
        
    }
    
    private static void checkState() {
        if (CLOSED.get()) {
            throw new IllegalStateException("WatchFileCenter already shutdown");
        }
    }
}
