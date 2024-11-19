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

import com.netty100.cluster.common.notify.Event;
import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.common.notify.listener.SmartSubscriber;
import com.netty100.cluster.consistency.DataOperation;
import com.netty100.cluster.core.distributed.distro.DistroProtocol;
import com.netty100.cluster.core.distributed.distro.component.DistroDataProcessor;
import com.netty100.cluster.core.distributed.distro.component.DistroDataStorage;
import com.netty100.cluster.core.distributed.distro.entity.DistroData;
import com.netty100.cluster.core.distributed.distro.entity.DistroKey;
import com.netty100.cluster.naming.cluster.transport.Serializer;
import com.netty100.cluster.naming.constants.ClientConstants;
import com.netty100.cluster.naming.core.v2.ServiceManager;
import com.netty100.cluster.naming.core.v2.client.Client;
import com.netty100.cluster.naming.core.v2.client.ClientSyncData;
import com.netty100.cluster.naming.core.v2.client.ClientSyncDatumSnapshot;
import com.netty100.cluster.naming.core.v2.client.manager.ClientManager;
import com.netty100.cluster.naming.core.v2.event.client.ClientEvent;
import com.netty100.cluster.naming.core.v2.event.client.ClientOperationEvent;
import com.netty100.cluster.naming.core.v2.event.publisher.NamingEventPublisherFactory;
import com.netty100.cluster.naming.core.v2.pojo.BatchInstanceData;
import com.netty100.cluster.naming.core.v2.pojo.BatchInstancePublishInfo;
import com.netty100.cluster.naming.core.v2.pojo.InstancePublishInfo;
import com.netty100.cluster.naming.core.v2.pojo.Service;
import com.netty100.cluster.naming.misc.Loggers;
import com.netty100.cluster.sys.env.EnvUtil;
import com.netty100.cluster.sys.utils.ApplicationUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Distro processor for v2.
 *
 * @author why
 */
public class DistroClientDataProcessor extends SmartSubscriber implements DistroDataStorage, DistroDataProcessor {
    
    public static final String TYPE = "Cap:Naming:v2:ClientData";
    
    private final ClientManager clientManager;
    
    private final DistroProtocol distroProtocol;
    
    private volatile boolean isFinishInitial;
    
    public DistroClientDataProcessor(ClientManager clientManager, DistroProtocol distroProtocol) {
        this.clientManager = clientManager;
        this.distroProtocol = distroProtocol;
        NotifyCenter.registerSubscriber(this, NamingEventPublisherFactory.getInstance());
    }
    
    @Override
    public void finishInitial() {
        isFinishInitial = true;
    }
    
    @Override
    public boolean isFinishInitial() {
        return isFinishInitial;
    }

    //增量数据同步
    //数据完成初始化后，节点的数据发生变化后需要讲增量数据同步到其他节点。
    //DistroClientDataProcessor类继承了SmartSubscriber，遵循Subscriber/Notify模式，当有订阅的事件时会进行回调通知。DistroClientDataProcessor订阅了ClientChangedEvent、ClientDisconnectEvent和ClientVerifyFailedEvent事件。
    @Override
    public List<Class<? extends Event>> subscribeTypes() {
        List<Class<? extends Event>> result = new LinkedList<>();
        result.add(ClientEvent.ClientChangedEvent.class);
        result.add(ClientEvent.ClientDisconnectEvent.class);
        result.add(ClientEvent.ClientVerifyFailedEvent.class);
        return result;
    }

    //当ClientChangedEvent事件发生时，DefaultPublisher会回调onEvent方法。
    @Override
    public void onEvent(Event event) {
        if (EnvUtil.getStandaloneMode()) {
            return;
        }
        if (event instanceof ClientEvent.ClientVerifyFailedEvent) {
            //发布校验失败事件
            syncToVerifyFailedServer((ClientEvent.ClientVerifyFailedEvent) event);
        } else {
            syncToAllServer((ClientEvent) event);
        }
    }
    
    private void syncToVerifyFailedServer(ClientEvent.ClientVerifyFailedEvent event) {
        //获取校验失败的目标Client
        Client client = clientManager.getClient(event.getClientId());
        if (null == client || !client.isEphemeral() || !clientManager.isResponsibleClient(client)) {
            return;
        }
        //当前处理类型为Nacos:Naming:v2:ClientData
        DistroKey distroKey = new DistroKey(client.getClientId(), TYPE);
        // Verify failed data should be sync directly.
        //syncToTarget方法，distroTaskEngineHolder发布延迟任务，调用DistroDelayTaskProcessor的process() 方法进行任务投递。执行变更任务 DistroSyncChangeTask向指定节点发送消息。校验失败同步的ADD流程与增量同步的CHANGE流程一致。
        distroProtocol.syncToTarget(distroKey, DataOperation.ADD, event.getTargetServer(), 0L);
    }

    /**
     * syncToAllServer方法调用DistroProtocol类的sync方法进行数据同步。
     */
    private void syncToAllServer(ClientEvent event) {
        Client client = event.getClient();
        // Only ephemeral data sync by Distro, persist client should sync by raft.
        if (null == client || !client.isEphemeral() || !clientManager.isResponsibleClient(client)) {
            return;
        }
        if (event instanceof ClientEvent.ClientDisconnectEvent) {
            DistroKey distroKey = new DistroKey(client.getClientId(), TYPE);
            distroProtocol.sync(distroKey, DataOperation.DELETE);
        } else if (event instanceof ClientEvent.ClientChangedEvent) {
            //节点变更事件，即增量数据的同步方法
            DistroKey distroKey = new DistroKey(client.getClientId(), TYPE);
            distroProtocol.sync(distroKey, DataOperation.CHANGE);
        }
    }
    
    @Override
    public String processType() {
        return TYPE;
    }

    //通过DistroClientDataProcessor的processData方法，调用handlerClientSyncData方法。
    @Override
    public boolean processData(DistroData distroData) {
        switch (distroData.getType()) {
            case ADD:
            case CHANGE:
                ClientSyncData clientSyncData = ApplicationUtils.getBean(Serializer.class)
                        .deserialize(distroData.getContent(), ClientSyncData.class);
                handlerClientSyncData(clientSyncData);
                return true;
            case DELETE:
                String deleteClientId = distroData.getDistroKey().getResourceKey();
                Loggers.DISTRO.info("[Client-Delete] Received distro client sync data {}", deleteClientId);
                clientManager.clientDisconnected(deleteClientId);
                return true;
            default:
                return false;
        }
    }
    
    private void handlerClientSyncData(ClientSyncData clientSyncData) {
        Loggers.DISTRO
                .info("[Client-Add] Received distro client sync data {}, revision={}", clientSyncData.getClientId(),
                        clientSyncData.getAttributes().getClientAttribute(ClientConstants.REVISION, 0L));
        // 因为是同步数据，因此创建IpPortBasedClient，并缓存
        clientManager.syncClientConnected(clientSyncData.getClientId(), clientSyncData.getAttributes());
        Client client = clientManager.getClient(clientSyncData.getClientId());
        // 升级此客户端的服务信息
        upgradeClient(client, clientSyncData);
    }
    
    private void upgradeClient(Client client, ClientSyncData clientSyncData) {
        // 已同步的服务集合
        Set<Service> syncedService = new HashSet<>();
        // process batch instance sync logic
        processBatchInstanceDistroData(syncedService, client, clientSyncData);
        List<String> namespaces = clientSyncData.getNamespaces();
        List<String> groupNames = clientSyncData.getGroupNames();
        List<String> serviceNames = clientSyncData.getServiceNames();
        List<InstancePublishInfo> instances = clientSyncData.getInstancePublishInfos();
        
        for (int i = 0; i < namespaces.size(); i++) {
            // 从获取的数据中构建一个Service对象
            Service service = Service.newService(namespaces.get(i), groupNames.get(i), serviceNames.get(i));
            Service singleton = ServiceManager.getInstance().getSingleton(service);
            // 标记此service已被处理
            syncedService.add(singleton);
            // 获取当前的实例
            InstancePublishInfo instancePublishInfo = instances.get(i);
            // 判断是否已经包含当前实例
            if (!instancePublishInfo.equals(client.getInstancePublishInfo(singleton))) {
                // 不包含则添加
                client.addServiceInstance(singleton, instancePublishInfo);
                // 当前节点发布服务注册事件
                NotifyCenter.publishEvent(
                        new ClientOperationEvent.ClientRegisterServiceEvent(singleton, client.getClientId()));
            }
        }
        // 若当前client内部已发布的service不在本次同步的列表内，说明已经过时了，要删掉
        for (Service each : client.getAllPublishedService()) {
            if (!syncedService.contains(each)) {
                client.removeServiceInstance(each);
                // 发布客户端下线事件
                NotifyCenter.publishEvent(new ClientOperationEvent.ClientDeregisterServiceEvent(each, client.getClientId()));
            }
        }
    }
    
    private static void processBatchInstanceDistroData(Set<Service> syncedService, Client client, ClientSyncData clientSyncData) {
        BatchInstanceData batchInstanceData = clientSyncData.getBatchInstanceData();
        if (batchInstanceData == null || CollectionUtils.isEmpty(batchInstanceData.getNamespaces())) {
            Loggers.DISTRO.info("[processBatchInstanceDistroData] BatchInstanceData is null , clientId is :{}", client.getClientId());
            return;
        }
        List<String> namespaces = batchInstanceData.getNamespaces();
        List<String> groupNames = batchInstanceData.getGroupNames();
        List<String> serviceNames = batchInstanceData.getServiceNames();
        List<BatchInstancePublishInfo> batchInstancePublishInfos = batchInstanceData.getBatchInstancePublishInfos();
        
        for (int i = 0; i < namespaces.size(); i++) {
            Service service = Service.newService(namespaces.get(i), groupNames.get(i), serviceNames.get(i));
            Service singleton = ServiceManager.getInstance().getSingleton(service);
            syncedService.add(singleton);
            BatchInstancePublishInfo batchInstancePublishInfo = batchInstancePublishInfos.get(i);
            BatchInstancePublishInfo targetInstanceInfo = (BatchInstancePublishInfo) client.getInstancePublishInfo(singleton);
            boolean result = false;
            if (targetInstanceInfo != null) {
                result = batchInstancePublishInfo.equals(targetInstanceInfo);
            }
            if (!result) {
                client.addServiceInstance(service, batchInstancePublishInfo);
                NotifyCenter.publishEvent(new ClientOperationEvent.ClientRegisterServiceEvent(singleton, client.getClientId()));
            }
        }
        client.setRevision(clientSyncData.getAttributes().<Integer>getClientAttribute(ClientConstants.REVISION, 0));
    }
    
    @Override
    public boolean processVerifyData(DistroData distroData, String sourceAddress) {
        // 对数据进行反序列化为DistroClientVerifyInfo对象
        DistroClientVerifyInfo verifyData = ApplicationUtils.getBean(Serializer.class)
                .deserialize(distroData.getContent(), DistroClientVerifyInfo.class);
        // 通过clientManager进行验证，clientManager是一个委托类，在Distro协议中，委托的就是EphemeralIpPortClientManager，因为ephemeral默认是true
        if (clientManager.verifyClient(verifyData)) {
            return true;
        }
        Loggers.DISTRO.info("client {} is invalid, get new client from {}", verifyData.getClientId(), sourceAddress);
        return false;
    }
    
    @Override
    public boolean processSnapshot(DistroData distroData) {
        // 反序列化获取的DistroData为ClientSyncDatumSnapshot
        ClientSyncDatumSnapshot snapshot = ApplicationUtils.getBean(Serializer.class)
                .deserialize(distroData.getContent(), ClientSyncDatumSnapshot.class);
        // 处理结果集，这里将返回远程节点负责的所有client以及client下面的service、instance信息
        for (ClientSyncData each : snapshot.getClientSyncDataList()) {
            // 每次处理一个client
            handlerClientSyncData(each);
        }
        return true;
    }
    
    @Override
    public DistroData getDistroData(DistroKey distroKey) {
        Client client = clientManager.getClient(distroKey.getResourceKey());
        if (null == client) {
            return null;
        }
        byte[] data = ApplicationUtils.getBean(Serializer.class).serialize(client.generateSyncData());
        return new DistroData(distroKey, data);
    }
    
    @Override
    public DistroData getDatumSnapshot() {
        List<ClientSyncData> datum = new LinkedList<>();
        //遍历EphemeralIpPortClientManager.clients所有的客户端对象
        for (String each : clientManager.allClientId()) {
            Client client = clientManager.getClient(each);
            //忽略持久化模式的客户端对象
            if (null == client || !client.isEphemeral()) {
                continue;
            }
            // Client对象填充Namespaces、GroupNames、ServiceNames等信息
            datum.add(client.generateSyncData());
        }
        ClientSyncDatumSnapshot snapshot = new ClientSyncDatumSnapshot();
        snapshot.setClientSyncDataList(datum);
        byte[] data = ApplicationUtils.getBean(Serializer.class).serialize(snapshot);
        return new DistroData(new DistroKey(DataOperation.SNAPSHOT.name(), TYPE), data);
    }
    
    @Override
    public List<DistroData> getVerifyData() {
        List<DistroData> result = null;
        // 遍历当前节点缓存的所有client
        for (String each : clientManager.allClientId()) {
            Client client = clientManager.getClient(each);
            if (null == client || !client.isEphemeral()) {
                continue;
            }
            //判断是否为本机负责
            if (clientManager.isResponsibleClient(client)) {
                // TODO add revision for client.
                DistroClientVerifyInfo verifyData = new DistroClientVerifyInfo(client.getClientId(), client.getRevision());
                DistroKey distroKey = new DistroKey(client.getClientId(), TYPE);
                DistroData data = new DistroData(distroKey, ApplicationUtils.getBean(Serializer.class).serialize(verifyData));
                data.setType(DataOperation.VERIFY);
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.add(data);
            }
        }
        return result;
    }
}
