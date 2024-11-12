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

package com.netty100.cluster.naming.consistency.ephemeral.distro.v2;

import com.netty100.cluster.core.cluster.ServerMemberManager;
import com.netty100.cluster.core.cluster.remote.ClusterRpcClientProxy;
import com.netty100.cluster.core.distributed.distro.DistroProtocol;
import com.netty100.cluster.core.distributed.distro.component.DistroComponentHolder;
import com.netty100.cluster.core.distributed.distro.component.DistroTransportAgent;
import com.netty100.cluster.core.distributed.distro.task.DistroTaskEngineHolder;
import com.netty100.cluster.naming.core.v2.client.Client;
import com.netty100.cluster.naming.core.v2.client.manager.ClientManager;
import com.netty100.cluster.naming.core.v2.client.manager.ClientManagerDelegate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Distro component registry for v2.
 *
 * @author yewenhai
 */
@Component
public class DistroClientComponentRegistry {
    
    private final ServerMemberManager serverMemberManager;
    
    private final DistroProtocol distroProtocol;
    
    private final DistroComponentHolder componentHolder;
    
    private final DistroTaskEngineHolder taskEngineHolder;
    
    private final ClientManager clientManager;
    
    private final ClusterRpcClientProxy clusterRpcClientProxy;
    
    public DistroClientComponentRegistry(ServerMemberManager serverMemberManager, DistroProtocol distroProtocol,
                                         DistroComponentHolder componentHolder, DistroTaskEngineHolder taskEngineHolder,
                                         ClientManagerDelegate clientManager, ClusterRpcClientProxy clusterRpcClientProxy) {
        this.serverMemberManager = serverMemberManager;
        this.distroProtocol = distroProtocol;
        this.componentHolder = componentHolder;
        this.taskEngineHolder = taskEngineHolder;
        this.clientManager = clientManager;
        this.clusterRpcClientProxy = clusterRpcClientProxy;
    }
    
    /**
     * Register necessary component to distro protocol for v2 {@link Client}
     * implement.
     */
    @PostConstruct
    public void doRegister() {
        // 创建distro处理器
        DistroClientDataProcessor dataProcessor = new DistroClientDataProcessor(clientManager, distroProtocol);
        // 创建distro传输代理对象
        DistroTransportAgent transportAgent = new DistroClientTransportAgent(clusterRpcClientProxy, serverMemberManager);
        // 创建失败处理器，该处理器主要是添加失败重试任务
        DistroClientTaskFailedHandler taskFailedHandler = new DistroClientTaskFailedHandler(taskEngineHolder);
        // 注册Nacos:Naming:v2:ClientData类型数据的数据仓库实现
        componentHolder.registerDataStorage(DistroClientDataProcessor.TYPE, dataProcessor);
        // 注册Nacos:Naming:v2:ClientData类型的DistroData数据处理器
        componentHolder.registerDataProcessor(dataProcessor);
        // 注册Nacos:Naming:v2:ClientData类型数据的数据传输代理对象实现
        componentHolder.registerTransportAgent(DistroClientDataProcessor.TYPE, transportAgent);
        // 注册Nacos:Naming:v2:ClientData类型的失败任务处理器
        componentHolder.registerFailedTaskHandler(DistroClientDataProcessor.TYPE, taskFailedHandler);
    }
}
