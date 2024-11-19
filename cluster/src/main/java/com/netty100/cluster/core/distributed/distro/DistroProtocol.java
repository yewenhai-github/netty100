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

package com.netty100.cluster.core.distributed.distro;

import com.netty100.cluster.consistency.DataOperation;
import com.netty100.cluster.core.cluster.Member;
import com.netty100.cluster.core.cluster.ServerMemberManager;
import com.netty100.cluster.core.distributed.distro.entity.DistroData;
import com.netty100.cluster.core.distributed.distro.entity.DistroKey;
import com.netty100.cluster.core.distributed.distro.task.DistroTaskEngineHolder;
import com.netty100.cluster.core.distributed.distro.task.delay.DistroDelayTask;
import com.netty100.cluster.core.distributed.distro.task.load.DistroLoadDataTask;
import com.netty100.cluster.core.distributed.distro.task.verify.DistroVerifyTimedTask;
import com.netty100.cluster.core.utils.GlobalExecutor;
import com.netty100.cluster.core.utils.Loggers;
import com.netty100.cluster.sys.env.EnvUtil;
import com.netty100.cluster.core.distributed.distro.component.DistroCallback;
import com.netty100.cluster.core.distributed.distro.component.DistroComponentHolder;
import com.netty100.cluster.core.distributed.distro.component.DistroDataProcessor;
import com.netty100.cluster.core.distributed.distro.component.DistroDataStorage;
import com.netty100.cluster.core.distributed.distro.component.DistroTransportAgent;
import org.springframework.stereotype.Component;

/**
 * Distro protocol.
 *
 * @author why
 */
@Component
public class DistroProtocol {
    
    private final ServerMemberManager memberManager;
    
    private final DistroComponentHolder distroComponentHolder;
    
    private final DistroTaskEngineHolder distroTaskEngineHolder;
    
    private volatile boolean isInitialized = false;
    
    public DistroProtocol(ServerMemberManager memberManager, DistroComponentHolder distroComponentHolder,
            DistroTaskEngineHolder distroTaskEngineHolder) {
        this.memberManager = memberManager;
        this.distroComponentHolder = distroComponentHolder;
        this.distroTaskEngineHolder = distroTaskEngineHolder;
        startDistroTask();
    }

    /**
     * 全量数据同步 在DistroProtocol类的构造方法中启动了一个startDistroTask()任务，其中包括了初始化同步任务 startLoadTask()。
     */
    private void startDistroTask() {
        if (EnvUtil.getStandaloneMode()) {
            isInitialized = true;
            return;
        }
        //验证任务，延迟5秒开始，间隔5秒轮询。
        startVerifyTask();
        //元数据任务
        startLoadTask();
    }

    private void startLoadTask() {
        DistroCallback loadCallback = new DistroCallback() {
            @Override
            public void onSuccess() {
                isInitialized = true;
            }
            
            @Override
            public void onFailed(Throwable throwable) {
                isInitialized = false;
            }
        };
        //startLoadTask()数据加载任务创建了一个DistroLoadDataTask任务，并传入了一个修改当前节点Distro协议完成状态的回调函数。
        GlobalExecutor.submitLoadDataTask(
                new DistroLoadDataTask(memberManager, distroComponentHolder, DistroConfig.getInstance(), loadCallback));
    }
    
    private void startVerifyTask() {
        GlobalExecutor.schedulePartitionDataTimedSync(new DistroVerifyTimedTask(memberManager, distroComponentHolder,
                        distroTaskEngineHolder.getExecuteWorkersManager()),
                DistroConfig.getInstance().getVerifyIntervalMillis());
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    /**
     * Start to sync by configured delay.
     *
     * @param distroKey distro key of sync data
     * @param action    the action of data operation
     */
    public void sync(DistroKey distroKey, DataOperation action) {
        sync(distroKey, action, DistroConfig.getInstance().getSyncDelayMillis());
    }
    
    /**
     * Start to sync data to all remote server.
     *
     * @param distroKey distro key of sync data
     * @param action    the action of data operation
     * @param delay     delay time for sync
     */
    public void sync(DistroKey distroKey, DataOperation action, long delay) {
        //向除本节点外的所有节点进行数据同步，对每个节点执行具体的同步逻辑syncToTarget方法。
        for (Member each : memberManager.allMembersWithoutSelf()) {
            syncToTarget(distroKey, action, each.getAddress(), delay);
        }
    }
    
    /**
     * Start to sync to target server.
     *
     * @param distroKey    distro key of sync data
     * @param action       the action of data operation
     * @param targetServer target server
     * @param delay        delay time for sync
     */
    public void syncToTarget(DistroKey distroKey, DataOperation action, String targetServer, long delay) {
        //调用distroTaskEngineHolder发布延迟任务。
        DistroKey distroKeyWithTarget = new DistroKey(distroKey.getResourceKey(), distroKey.getResourceType(),
                targetServer);
        DistroDelayTask distroDelayTask = new DistroDelayTask(distroKeyWithTarget, action, delay);
        // 调用DistroDelayTaskProcessor的process() 方法进行任务投递。执行变更任务 DistroSyncChangeTask向指定节点发送消息。
        distroTaskEngineHolder.getDelayTaskExecuteEngine().addTask(distroKeyWithTarget, distroDelayTask);
        if (Loggers.DISTRO.isDebugEnabled()) {
            Loggers.DISTRO.debug("[DISTRO-SCHEDULE] {} to {}", distroKey, targetServer);
        }
    }
    
    /**
     * Query data from specified server.
     *
     * @param distroKey data key
     * @return data
     */
    public DistroData queryFromRemote(DistroKey distroKey) {
        if (null == distroKey.getTargetServer()) {
            Loggers.DISTRO.warn("[DISTRO] Can't query data from empty server");
            return null;
        }
        String resourceType = distroKey.getResourceType();
        DistroTransportAgent transportAgent = distroComponentHolder.findTransportAgent(resourceType);
        if (null == transportAgent) {
            Loggers.DISTRO.warn("[DISTRO] Can't find transport agent for key {}", resourceType);
            return null;
        }
        return transportAgent.getData(distroKey, distroKey.getTargetServer());
    }
    
    /**
     * Receive synced distro data, find processor to process.
     *
     * @param distroData Received data
     * @return true if handle receive data successfully, otherwise false
     */
    public boolean onReceive(DistroData distroData) {
        Loggers.DISTRO.info("[DISTRO] Receive distro data type: {}, key: {}", distroData.getType(), distroData.getDistroKey());
        String resourceType = distroData.getDistroKey().getResourceType();
        //DistroClientDataProcessor
        DistroDataProcessor dataProcessor = distroComponentHolder.findDataProcessor(resourceType);
        if (null == dataProcessor) {
            Loggers.DISTRO.warn("[DISTRO] Can't find data process for received data {}", resourceType);
            return false;
        }
        return dataProcessor.processData(distroData);
    }
    
    /**
     * Receive verify data, find processor to process.
     *
     * @param distroData    verify data
     * @param sourceAddress source server address, might be get data from source server
     * @return true if verify data successfully, otherwise false
     */
    public boolean onVerify(DistroData distroData, String sourceAddress) {
        if (Loggers.DISTRO.isDebugEnabled()) {
            Loggers.DISTRO.debug("[DISTRO] Receive verify data type: {}, key: {}", distroData.getType(),
                    distroData.getDistroKey());
        }
        // 根据此次处理的数据类型获取对应的处理器
        String resourceType = distroData.getDistroKey().getResourceType();
        //DistroDataProcessor处理器使用EphemeralIpPortClientManager进行处理。
        DistroDataProcessor dataProcessor = distroComponentHolder.findDataProcessor(resourceType);
        if (null == dataProcessor) {
            Loggers.DISTRO.warn("[DISTRO] Can't find verify data process for received data {}", resourceType);
            return false;
        }
        //处理客户端的注册信息
        return dataProcessor.processVerifyData(distroData, sourceAddress);
    }
    
    /**
     * Query data of input distro key.
     *
     * @param distroKey key of data
     * @return data
     */
    public DistroData onQuery(DistroKey distroKey) {
        String resourceType = distroKey.getResourceType();
        DistroDataStorage distroDataStorage = distroComponentHolder.findDataStorage(resourceType);
        if (null == distroDataStorage) {
            Loggers.DISTRO.warn("[DISTRO] Can't find data storage for received key {}", resourceType);
            return new DistroData(distroKey, new byte[0]);
        }
        return distroDataStorage.getDistroData(distroKey);
    }
    
    /**
     * Query all datum snapshot.
     *
     * @param type datum type
     * @return all datum snapshot
     */
    public DistroData onSnapshot(String type) {
        DistroDataStorage distroDataStorage = distroComponentHolder.findDataStorage(type);
        if (null == distroDataStorage) {
            Loggers.DISTRO.warn("[DISTRO] Can't find data storage for received key {}", type);
            return new DistroData(new DistroKey("snapshot", type), new byte[0]);
        }
        return distroDataStorage.getDatumSnapshot();
    }
}
