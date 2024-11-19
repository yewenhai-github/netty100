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

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.remote.RequestCallBack;
import com.netty100.cluster.api.remote.response.Response;
import com.netty100.cluster.api.remote.response.ResponseCode;
import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.consistency.DataOperation;
import com.netty100.cluster.core.cluster.Member;
import com.netty100.cluster.core.cluster.NodeState;
import com.netty100.cluster.core.cluster.ServerMemberManager;
import com.netty100.cluster.core.cluster.remote.ClusterRpcClientProxy;
import com.netty100.cluster.core.distributed.distro.DistroConfig;
import com.netty100.cluster.core.distributed.distro.component.DistroCallback;
import com.netty100.cluster.core.distributed.distro.component.DistroTransportAgent;
import com.netty100.cluster.core.distributed.distro.entity.DistroData;
import com.netty100.cluster.core.distributed.distro.entity.DistroKey;
import com.netty100.cluster.core.distributed.distro.exception.DistroException;
import com.netty100.cluster.naming.cluster.remote.request.DistroDataRequest;
import com.netty100.cluster.naming.cluster.remote.response.DistroDataResponse;
import com.netty100.cluster.naming.core.v2.event.client.ClientEvent;
import com.netty100.cluster.naming.misc.GlobalExecutor;
import com.netty100.cluster.naming.misc.Loggers;

import java.util.concurrent.Executor;

/**
 * Distro transport agent for v2.
 *
 * @author why
 */
public class DistroClientTransportAgent implements DistroTransportAgent {
    
    private final ClusterRpcClientProxy clusterRpcClientProxy;
    
    private final ServerMemberManager memberManager;
    
    public DistroClientTransportAgent(ClusterRpcClientProxy clusterRpcClientProxy,
            ServerMemberManager serverMemberManager) {
        this.clusterRpcClientProxy = clusterRpcClientProxy;
        this.memberManager = serverMemberManager;
    }
    
    @Override
    public boolean supportCallbackTransport() {
        return true;
    }
    //使用DistroClientTransportAgent进行实际的数据发送。
    @Override
    public boolean syncData(DistroData data, String targetServer) {
        if (isNoExistTarget(targetServer)) {
            return true;
        }
        DistroDataRequest request = new DistroDataRequest(data, data.getType());
        Member member = memberManager.find(targetServer);
        if (checkTargetServerStatusUnhealthy(member)) {
            Loggers.DISTRO
                    .warn("[DISTRO] Cancel distro sync caused by target server {} unhealthy, key: {}", targetServer,
                            data.getDistroKey());
            return false;
        }
        try {
            // 使用Rpc代理对象发送同步rpc请求，DistroDataRequestHandler.hand处理
            Response response = clusterRpcClientProxy.sendRequest(member, request);
            return checkResponse(response);
        } catch (CapException e) {
            Loggers.DISTRO.error("[DISTRO-FAILED] Sync distro data failed! key: {}", data.getDistroKey(), e);
        }
        return false;
    }
    
    @Override
    public void syncData(DistroData data, String targetServer, DistroCallback callback) {
        if (isNoExistTarget(targetServer)) {
            callback.onSuccess();
            return;
        }
        DistroDataRequest request = new DistroDataRequest(data, data.getType());
        Member member = memberManager.find(targetServer);
        if (checkTargetServerStatusUnhealthy(member)) {
//            Loggers.DISTRO.warn("[DISTRO-1] Cancel distro sync caused by target server {} unhealthy, key: {}", targetServer, data.getDistroKey());
            callback.onFailed(null);
            return;
        }
        try {
            clusterRpcClientProxy.asyncRequest(member, request, new DistroRpcCallbackWrapper(callback, member));
        } catch (CapException capException) {
            callback.onFailed(capException);
        }
    }
    
    @Override
    public boolean syncVerifyData(DistroData verifyData, String targetServer) {
        if (isNoExistTarget(targetServer)) {
            return true;
        }
        // replace target server as self server so that can callback.
        verifyData.getDistroKey().setTargetServer(memberManager.getSelf().getAddress());
        DistroDataRequest request = new DistroDataRequest(verifyData, DataOperation.VERIFY);
        Member member = memberManager.find(targetServer);
        if (checkTargetServerStatusUnhealthy(member)) {
            Loggers.DISTRO.warn("[DISTRO-2] Cancel distro verify caused by target server {} unhealthy, key: {}", targetServer, verifyData.getDistroKey());
            return false;
        }
        try {
            Response response = clusterRpcClientProxy.sendRequest(member, request);
            return checkResponse(response);
        } catch (CapException e) {
            Loggers.DISTRO.error("[DISTRO-FAILED] Verify distro data failed! key: {} ", verifyData.getDistroKey(), e);
        }
        return false;
    }
    
    @Override
    public void syncVerifyData(DistroData verifyData, String targetServer, DistroCallback callback) {
        if (isNoExistTarget(targetServer)) {
            callback.onSuccess();
            return;
        }
        DistroDataRequest request = new DistroDataRequest(verifyData, DataOperation.VERIFY);
        Member member = memberManager.find(targetServer);
        if (checkTargetServerStatusUnhealthy(member)) {
            Loggers.DISTRO
                    .warn("[DISTRO] Cancel distro verify caused by target server {} unhealthy, key: {}", targetServer,
                            verifyData.getDistroKey());
            callback.onFailed(null);
            return;
        }
        try {
            //请求回调类DistroVerifyCallbackWrapper类关键是回调方法内容，校验失败发布ClientVerifyFailedEvent事件。
            DistroVerifyCallbackWrapper wrapper = new DistroVerifyCallbackWrapper(targetServer,
                    verifyData.getDistroKey().getResourceKey(), callback, member);
            clusterRpcClientProxy.asyncRequest(member, request, wrapper);
        } catch (CapException capException) {
            callback.onFailed(capException);
        }
    }
    
    @Override
    public DistroData getData(DistroKey key, String targetServer) {
        // 从节点管理器获取目标节点信息
        Member member = memberManager.find(targetServer);
        // 判断目标服务器是否健康
        if (checkTargetServerStatusUnhealthy(member)) {
            throw new DistroException(
                    String.format("[DISTRO] Cancel get snapshot caused by target server %s unhealthy", targetServer));
        }
        // 构建请求参数
        DistroDataRequest request = new DistroDataRequest();
        DistroData distroData = new DistroData();
        distroData.setDistroKey(key);
        distroData.setType(DataOperation.QUERY);
        request.setDistroData(distroData);
        request.setDataOperation(DataOperation.QUERY);
        try {
            // 使用Rpc代理对象发送同步rpc请求
            Response response = clusterRpcClientProxy.sendRequest(member, request);
            if (checkResponse(response)) {
                return ((DistroDataResponse) response).getDistroData();
            } else {
                throw new DistroException(
                        String.format("[DISTRO-FAILED] Get data request to %s failed, code: %d, message: %s",
                                targetServer, response.getErrorCode(), response.getMessage()));
            }
        } catch (CapException e) {
            throw new DistroException("[DISTRO-FAILED] Get distro data failed! ", e);
        }
    }
    
    @Override
    public DistroData getDatumSnapshot(String targetServer) {
        // 从节点管理器获取目标节点信息
        Member member = memberManager.find(targetServer);
        // 判断目标服务器是否健康
        if (checkTargetServerStatusUnhealthy(member)) {
            throw new DistroException(
                    String.format("[DISTRO] Cancel get snapshot caused by target server %s unhealthy", targetServer));
        }
        // 构建请求参数
        DistroDataRequest request = new DistroDataRequest();
        // 设置请求的操作类型为DataOperation.SNAPSHOT
        request.setDataOperation(DataOperation.SNAPSHOT);
        try {
            // 使用Rpc代理对象发送同步rpc请求
            Response response = clusterRpcClientProxy
                    .sendRequest(member, request, DistroConfig.getInstance().getLoadDataTimeoutMillis());
            if (checkResponse(response)) {
                return ((DistroDataResponse) response).getDistroData();
            } else {
                throw new DistroException(
                        String.format("[DISTRO-FAILED] Get snapshot request to %s failed, code: %d, message: %s",
                                targetServer, response.getErrorCode(), response.getMessage()));
            }
        } catch (CapException e) {
            throw new DistroException("[DISTRO-FAILED] Get distro snapshot failed! ", e);
        }
    }
    
    private boolean isNoExistTarget(String target) {
        return !memberManager.hasMember(target);
    }
    
    private boolean checkTargetServerStatusUnhealthy(Member member) {
        return null == member || !NodeState.UP.equals(member.getState()) || !clusterRpcClientProxy.isRunning(member);
    }
    
    private boolean checkResponse(Response response) {
        return ResponseCode.SUCCESS.getCode() == response.getResultCode();
    }
    
    private class DistroRpcCallbackWrapper implements RequestCallBack<Response> {
        
        private final DistroCallback distroCallback;
        
        private final Member member;
        
        public DistroRpcCallbackWrapper(DistroCallback distroCallback, Member member) {
            this.distroCallback = distroCallback;
            this.member = member;
        }
        
        @Override
        public Executor getExecutor() {
            return GlobalExecutor.getCallbackExecutor();
        }
        
        @Override
        public long getTimeout() {
            return DistroConfig.getInstance().getSyncTimeoutMillis();
        }
        
        @Override
        public void onResponse(Response response) {
            if (checkResponse(response)) {
//                NamingTpsMonitor.distroSyncSuccess(member.getAddress(), member.getIp());
                distroCallback.onSuccess();
            } else {
//                NamingTpsMonitor.distroSyncFail(member.getAddress(), member.getIp());
                distroCallback.onFailed(null);
            }
        }
        
        @Override
        public void onException(Throwable e) {
            distroCallback.onFailed(e);
        }
    }
    
    private class DistroVerifyCallbackWrapper implements RequestCallBack<Response> {
        
        private final String targetServer;
        
        private final String clientId;
        
        private final DistroCallback distroCallback;
        
        private final Member member;
        
        private DistroVerifyCallbackWrapper(String targetServer, String clientId, DistroCallback distroCallback,
                Member member) {
            this.targetServer = targetServer;
            this.clientId = clientId;
            this.distroCallback = distroCallback;
            this.member = member;
        }
        
        @Override
        public Executor getExecutor() {
            return GlobalExecutor.getCallbackExecutor();
        }
        
        @Override
        public long getTimeout() {
            return DistroConfig.getInstance().getVerifyTimeoutMillis();
        }
        
        @Override
        public void onResponse(Response response) {
            if (checkResponse(response)) {
//                NamingTpsMonitor.distroVerifySuccess(member.getAddress(), member.getIp());
                distroCallback.onSuccess();
            } else {
                Loggers.DISTRO.info("Target {} verify client {} failed, sync new client", targetServer, clientId);
                // 校验失败发布ClientVerifyFailedEvent事件
                NotifyCenter.publishEvent(new ClientEvent.ClientVerifyFailedEvent(clientId, targetServer));
//                NamingTpsMonitor.distroVerifyFail(member.getAddress(), member.getIp());
                distroCallback.onFailed(null);
            }
        }
        
        @Override
        public void onException(Throwable e) {
            distroCallback.onFailed(e);
        }
    }
}
