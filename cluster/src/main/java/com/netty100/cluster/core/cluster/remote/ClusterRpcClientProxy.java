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

package com.netty100.cluster.core.cluster.remote;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.remote.RemoteConstants;
import com.netty100.cluster.api.remote.RequestCallBack;
import com.netty100.cluster.api.remote.request.Request;
import com.netty100.cluster.api.remote.response.Response;
import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.common.remote.ConnectionType;
import com.netty100.cluster.common.remote.client.RpcClient;
import com.netty100.cluster.common.remote.client.RpcClientFactory;
import com.netty100.cluster.common.remote.client.ServerListFactory;
import com.netty100.cluster.common.utils.CollectionUtils;
import com.netty100.cluster.core.cluster.Member;
import com.netty100.cluster.core.cluster.MemberChangeListener;
import com.netty100.cluster.core.cluster.MembersChangeEvent;
import com.netty100.cluster.core.cluster.ServerMemberManager;
import com.netty100.cluster.core.utils.Loggers;
import com.netty100.cluster.sys.env.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.netty100.cluster.api.exception.CapException.CLIENT_INVALID_PARAM;

/**
 * cluster rpc client proxy.
 *
 * @author yewenhai
 * @version $Id: ClusterRpcClientProxy.java, v 0.1 2020年08月11日 2:11 PM liuzunfei Exp $
 */
@Service
public class ClusterRpcClientProxy extends MemberChangeListener {
    
    private static final long DEFAULT_REQUEST_TIME_OUT = 3000L;
    
    @Autowired
    ServerMemberManager serverMemberManager;
    
    /**
     * init after constructor.
     */
    @PostConstruct
    public void init() {
        try {
            NotifyCenter.registerSubscriber(this);
            List<Member> members = serverMemberManager.allMembersWithoutSelf();
            refresh(members);
            Loggers.CLUSTER
                    .info("[ClusterRpcClientProxy] success to refresh cluster rpc client on start up,members ={} ",
                            members);
        } catch (CapException e) {
            Loggers.CLUSTER.warn("[ClusterRpcClientProxy] fail to refresh cluster rpc client,{} ", e.getMessage());
        }
        
    }
    
    /**
     * init cluster rpc clients.
     * ClusterRpcClientProxy初始化和发生MembersChangeEvent事件的时候会执行refresh(members);方法。
     * @param members cluster server list member list.
     */
    private void refresh(List<Member> members) throws CapException {
        
        //循环集群内机器：创建RpcClient 对象和调用RpcClient 的start方法。
        for (Member member : members) {
            createRpcClientAndStart(member, ConnectionType.GRPC);
        }
        
        //shutdown and remove old members.
        // 销毁服务器下线的客户端连接缓存
        Set<Map.Entry<String, RpcClient>> allClientEntrys = RpcClientFactory.getAllClientEntries();
        Iterator<Map.Entry<String, RpcClient>> iterator = allClientEntrys.iterator();
        List<String> newMemberKeys = members.stream().map(this::memberClientKey).collect(Collectors.toList());
        while (iterator.hasNext()) {
            Map.Entry<String, RpcClient> next1 = iterator.next();
            if (next1.getKey().startsWith("Cluster-") && !newMemberKeys.contains(next1.getKey())) {
                Loggers.CLUSTER.info("member leave,destroy client of member - > : {}", next1.getKey());
                RpcClientFactory.getClient(next1.getKey()).shutdown();
                iterator.remove();
            }
        }
        
    }
    
    private String memberClientKey(Member member) {
        return "Cluster-" + member.getAddress();
    }
    
    private void createRpcClientAndStart(Member member, ConnectionType type) throws CapException {
        Map<String, String> labels = new HashMap<>(2);
        labels.put(RemoteConstants.LABEL_SOURCE, RemoteConstants.LABEL_SOURCE_CLUSTER);
        String memberClientKey = memberClientKey(member);
        RpcClient client = buildRpcClient(type, labels, memberClientKey);
        if (!client.getConnectionType().equals(type)) {
            Loggers.CLUSTER.info("connection type changed,destroy client of member - > : {}", member);
            RpcClientFactory.destroyClient(memberClientKey);
            client = buildRpcClient(type, labels, memberClientKey);
        }
        
        if (client.isWaitInitiated()) {
            Loggers.CLUSTER.info("start a new rpc client to member - > : {}", member);
            
            //one fixed server
            client.serverListFactory(new ServerListFactory() {
                @Override
                public String genNextServer() {
                    return member.getAddress();
                }
                
                @Override
                public String getCurrentServer() {
                    return member.getAddress();
                }
                
                @Override
                public List<String> getServerList() {
                    return CollectionUtils.list(member.getAddress());
                }
            });
            
            client.start();
        }
    }
    
    /**
     * Using {@link EnvUtil#getAvailableProcessors(int)} to build cluster clients' grpc thread pool.
     */
    private RpcClient buildRpcClient(ConnectionType type, Map<String, String> labels, String memberClientKey) {
        RpcClient clusterClient = RpcClientFactory
                .createClusterClient(memberClientKey, type, EnvUtil.getAvailableProcessors(2),
                        EnvUtil.getAvailableProcessors(8), labels);
        return clusterClient;
    }
    
    /**
     * send request to member.
     *
     * @param member  member of server.
     * @param request request.
     * @return Response response.
     * @throws CapException exception may throws.
     */
    public Response sendRequest(Member member, Request request) throws CapException {
        return sendRequest(member, request, DEFAULT_REQUEST_TIME_OUT);
    }
    
    /**
     * send request to member.
     *
     * @param member  member of server.
     * @param request request.
     * @return Response response.
     * @throws CapException exception may throws.
     */
    public Response sendRequest(Member member, Request request, long timeoutMills) throws CapException {
        RpcClient client = RpcClientFactory.getClient(memberClientKey(member));
        if (client != null) {
            return client.request(request, timeoutMills);
        } else {
            throw new CapException(CLIENT_INVALID_PARAM, "No rpc client related to member: " + member);
        }
    }
    
    /**
     * aync send request to member with callback.
     *
     * @param member   member of server.
     * @param request  request.
     * @param callBack RequestCallBack.
     * @throws CapException exception may throws.
     */
    public void asyncRequest(Member member, Request request, RequestCallBack callBack) throws CapException {
        RpcClient client = RpcClientFactory.getClient(memberClientKey(member));
        if (client != null) {
            client.asyncRequest(request, callBack);
        } else {
            throw new CapException(CLIENT_INVALID_PARAM, "No rpc client related to member: " + member);
        }
    }
    
    /**
     * send request to member.
     *
     * @param request request.
     * @throws CapException exception may throw.
     */
    public void sendRequestToAllMembers(Request request) throws CapException {
        List<Member> members = serverMemberManager.allMembersWithoutSelf();
        for (Member member1 : members) {
            sendRequest(member1, request);
        }
    }

    // ClusterRpcClientProxy继承了Subscriber，所以先将自己注册成订阅者，发生MembersChangeEvent事件的时候，会执行onEvent方法
    @Override
    public void onEvent(MembersChangeEvent event) {
        try {
            List<Member> members = serverMemberManager.allMembersWithoutSelf();
            refresh(members);
        } catch (CapException e) {
            Loggers.CLUSTER.warn("[serverlist] fail to refresh cluster rpc client, event:{}, msg: {} ", event, e.getMessage());
        }
    }
    
    /**
     * Check whether client for member is running.
     *
     * @param member member
     * @return {@code true} if target client is connected, otherwise {@code false}
     */
    public boolean isRunning(Member member) {
        RpcClient client = RpcClientFactory.getClient(memberClientKey(member));
        if (null == client) {
            return false;
        }
        return client.isRunning();
    }
}
