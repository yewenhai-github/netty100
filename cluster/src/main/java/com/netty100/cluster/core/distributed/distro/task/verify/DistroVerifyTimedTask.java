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

package com.netty100.cluster.core.distributed.distro.task.verify;

import com.netty100.cluster.core.cluster.Member;
import com.netty100.cluster.core.cluster.ServerMemberManager;
import com.netty100.cluster.core.distributed.distro.component.DistroComponentHolder;
import com.netty100.cluster.core.distributed.distro.component.DistroDataStorage;
import com.netty100.cluster.core.distributed.distro.component.DistroTransportAgent;
import com.netty100.cluster.core.distributed.distro.entity.DistroData;
import com.netty100.cluster.core.utils.Loggers;
import com.netty100.cluster.core.distributed.distro.task.execute.DistroExecuteTaskExecuteEngine;

import java.util.List;

/**
 * Timed to start distro verify task.
 *
 * @author why
 */
public class DistroVerifyTimedTask implements Runnable {
    
    private final ServerMemberManager serverMemberManager;
    
    private final DistroComponentHolder distroComponentHolder;
    
    private final DistroExecuteTaskExecuteEngine executeTaskExecuteEngine;
    
    public DistroVerifyTimedTask(ServerMemberManager serverMemberManager, DistroComponentHolder distroComponentHolder,
            DistroExecuteTaskExecuteEngine executeTaskExecuteEngine) {
        this.serverMemberManager = serverMemberManager;
        this.distroComponentHolder = distroComponentHolder;
        this.executeTaskExecuteEngine = executeTaskExecuteEngine;
    }

    /**
     * 向其他所有节点发起每种类型的验证，调用verifyForDataStorage方法。
     */
    @Override
    public void run() {
        try {
            //获取除本节点外的其他节点
            List<Member> targetServer = serverMemberManager.allMembersWithoutSelf();
            if (Loggers.DISTRO.isDebugEnabled()) {
                Loggers.DISTRO.debug("server list is: {}", targetServer);
            }
            //每一种类型的数据都需要发起验证
            for (String each : distroComponentHolder.getDataStorageTypes()) {
                // 对dataStorage内的数据进行验证
                verifyForDataStorage(each, targetServer);
            }
        } catch (Exception e) {
            Loggers.DISTRO.error("[DISTRO-FAILED] verify task failed.", e);
        }
    }

    /**
     * 调用getVerifyData方法从DistroDataStorage中获取数据，并对每一个节点的所有验证数据都创建了一个新的任务DistroVerifyExecuteTask，由它来执行具体的验证工作。
     * @param type
     * @param targetServer
     */
    private void verifyForDataStorage(String type, List<Member> targetServer) {
        DistroDataStorage dataStorage = distroComponentHolder.findDataStorage(type);
        // 若数据还未同步完毕则不处理
        if (!dataStorage.isFinishInitial()) {
            Loggers.DISTRO.warn("data storage {} has not finished initial step, do not send verify data",
                    dataStorage.getClass().getSimpleName());
            return;
        }
        //从DistroDataStorage中获取本机负责的Client信息(TODO 获取客户端连接信息)
        List<DistroData> verifyData = dataStorage.getVerifyData();
        if (null == verifyData || verifyData.isEmpty()) {
            return;
        }
        // 对每个节点开启一个异步的线程来执行
        for (Member member : targetServer) {
            DistroTransportAgent agent = distroComponentHolder.findTransportAgent(type);
            if (null == agent) {
                continue;
            }
            executeTaskExecuteEngine.addTask(member.getAddress() + type,
                    new DistroVerifyExecuteTask(agent, verifyData, member.getAddress(), type));
        }
    }
}
