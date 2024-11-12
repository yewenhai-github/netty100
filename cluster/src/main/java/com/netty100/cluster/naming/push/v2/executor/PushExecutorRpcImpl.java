/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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

package com.netty100.cluster.naming.push.v2.executor;

import com.netty100.cluster.api.naming.pojo.ServiceInfo;
import com.netty100.cluster.api.naming.remote.request.NotifySubscriberRequest;
import com.netty100.cluster.core.remote.RpcPushService;
import com.netty100.cluster.naming.misc.GlobalExecutor;
import com.netty100.cluster.naming.pojo.Subscriber;
import com.netty100.cluster.naming.push.v2.PushDataWrapper;
import com.netty100.cluster.naming.push.v2.task.NamingPushCallback;
import com.netty100.cluster.naming.utils.ServiceUtil;
import org.springframework.stereotype.Component;

/**
 * Push execute service for rpc.
 *
 * @author yewenhai
 */
@Component
public class PushExecutorRpcImpl implements PushExecutor {
    
    private final RpcPushService pushService;
    
    public PushExecutorRpcImpl(RpcPushService pushService) {
        this.pushService = pushService;
    }
    
    @Override
    public void doPush(String clientId, Subscriber subscriber, PushDataWrapper data) {
        pushService.pushWithoutAck(clientId,
                NotifySubscriberRequest.buildNotifySubscriberRequest(getServiceInfo(data, subscriber)));
    }
    
    @Override
    public void doPushWithCallback(String clientId, Subscriber subscriber, PushDataWrapper data,
            NamingPushCallback callBack) {
        ServiceInfo actualServiceInfo = getServiceInfo(data, subscriber);
        callBack.setActualServiceInfo(actualServiceInfo);
        pushService.pushWithCallback(clientId, NotifySubscriberRequest.buildNotifySubscriberRequest(actualServiceInfo),
                callBack, GlobalExecutor.getCallbackExecutor());
    }
    
    private ServiceInfo getServiceInfo(PushDataWrapper data, Subscriber subscriber) {
        return ServiceUtil
                .selectInstancesWithHealthyProtection(data.getOriginalData(), data.getServiceMetadata(), false, true,
                        subscriber);
    }
}
