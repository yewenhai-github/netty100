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

package com.netty100.cluster.naming.healthcheck.heartbeat;

import com.netty100.cluster.common.task.AbstractExecuteTask;
import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import com.netty100.cluster.naming.core.v2.pojo.HealthCheckInstancePublishInfo;
import com.netty100.cluster.naming.core.v2.pojo.InstancePublishInfo;

/**
 * Client beat update task.
 *
 * @author yewenhai
 */
public class ClientBeatUpdateTask extends AbstractExecuteTask {
    
    private final IpPortBasedClient client;
    
    public ClientBeatUpdateTask(IpPortBasedClient client) {
        this.client = client;
    }
    
    @Override
    public void run() {
        // 获取当前时间，更新Client和Client下的Instance的最新活跃时间
        long currentTime = System.currentTimeMillis();
        for (InstancePublishInfo each : client.getAllInstancePublishInfo()) {
            ((HealthCheckInstancePublishInfo) each).setLastHeartBeatTime(currentTime);
        }
        // 更新client的最新更新时间
        client.setLastUpdatedTime();

        //以上是校验成功的处理流程，而如果校验失败，就会触发回调函数，发布ClientVerifyFailedEvent事件。
        //DistroClientDataProcessor类订阅了ClientVerifyFailedEvent事件，当ClientVerifyFailedEvent事件发生时，DefaultPublisher会回调onEvent方法。调用syncToVerifyFailedServer方法进行校验失败后的数据同步处理。
    }
}
