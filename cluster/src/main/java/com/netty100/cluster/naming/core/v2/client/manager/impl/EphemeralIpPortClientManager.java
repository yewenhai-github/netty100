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

package com.netty100.cluster.naming.core.v2.client.manager.impl;

import com.netty100.cluster.api.common.Constants;
import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.naming.consistency.ephemeral.distro.v2.DistroClientVerifyInfo;
import com.netty100.cluster.naming.constants.ClientConstants;
import com.netty100.cluster.naming.core.DistroMapper;
import com.netty100.cluster.naming.core.v2.client.Client;
import com.netty100.cluster.naming.core.v2.client.ClientAttributes;
import com.netty100.cluster.naming.core.v2.client.factory.ClientFactory;
import com.netty100.cluster.naming.core.v2.client.factory.ClientFactoryHolder;
import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import com.netty100.cluster.naming.core.v2.client.manager.ClientManager;
import com.netty100.cluster.naming.core.v2.event.client.ClientEvent;
import com.netty100.cluster.naming.healthcheck.heartbeat.ClientBeatUpdateTask;
import com.netty100.cluster.naming.misc.ClientConfig;
import com.netty100.cluster.naming.misc.Loggers;
import com.netty100.cluster.naming.misc.NamingExecuteTaskDispatcher;
import com.netty100.cluster.naming.misc.SwitchDomain;
import com.netty100.cluster.sys.utils.ClientCacheUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * The manager of {@code IpPortBasedClient} and ephemeral.
 *
 * @author why
 */
@DependsOn("clientServiceIndexesManager")
@Component("ephemeralIpPortClientManager")
public class EphemeralIpPortClientManager implements ClientManager {
    
    private final DistroMapper distroMapper;
    
    private final ClientFactory<IpPortBasedClient> clientFactory;
    
    public EphemeralIpPortClientManager(DistroMapper distroMapper, SwitchDomain switchDomain) {
        this.distroMapper = distroMapper;
//        GlobalExecutor.scheduleExpiredClientCleaner(new ExpiredClientCleaner(this, switchDomain), 0,
//                Constants.DEFAULT_HEART_BEAT_INTERVAL, TimeUnit.MILLISECONDS);
        clientFactory = ClientFactoryHolder.getInstance().findClientFactory(ClientConstants.EPHEMERAL_IP_PORT);
    }
    
    @Override
    public boolean clientConnected(String clientId, ClientAttributes attributes) {
        return clientConnected(clientFactory.newClient(clientId, attributes));
    }
    
    @Override
    public boolean clientConnected(final Client client) {
        ClientCacheUtils.getClients().put(client.getClientId(), (IpPortBasedClient) client);
        return true;
    }
    
    @Override
    public boolean syncClientConnected(String clientId, ClientAttributes attributes) {
        return clientConnected(clientFactory.newSyncedClient(clientId, attributes));
    }
    
    @Override
    public boolean clientDisconnected(String clientId) {
        Loggers.SRV_LOG.info("Client connection {} disconnect, remove instances and subscribers", clientId);
        IpPortBasedClient client = ClientCacheUtils.getClients().remove(clientId);
        if (null == client) {
            return true;
        }
        NotifyCenter.publishEvent(new ClientEvent.ClientDisconnectEvent(client, isResponsibleClient(client)));
        client.release();
        return true;
    }
    
    @Override
    public Client getClient(String clientId) {
        return ClientCacheUtils.getClients().get(clientId);
    }
    
    @Override
    public boolean contains(String clientId) {
        return ClientCacheUtils.getClients().containsKey(clientId);
    }
    
    @Override
    public Collection<String> allClientId() {
        return ClientCacheUtils.getClients().keySet();
    }
    
    @Override
    public boolean isResponsibleClient(Client client) {
        if (client instanceof IpPortBasedClient) {
            String responsibleId = ((IpPortBasedClient) client).getResponsibleId();
            if(null == responsibleId){
                return false;
            }
            return distroMapper.responsible(responsibleId);
        }
        return false;
    }
    
    @Override
    public boolean verifyClient(DistroClientVerifyInfo verifyData) {
        String clientId = verifyData.getClientId();
        IpPortBasedClient client = ClientCacheUtils.getClients().get(clientId);
        if (null != client) {
            // remote node of old version will always verify with zero revision
            if (0 == verifyData.getRevision() || client.getRevision() == verifyData.getRevision()) {
                // 如果版本一致，执行ClientBeatUpdateTask.run()，这个方法更新了Client最新的心跳时间、最新修改时间
                NamingExecuteTaskDispatcher.getInstance()
                        .dispatchAndExecuteTask(clientId, new ClientBeatUpdateTask(client));
                return true;
            } else {
                Loggers.DISTRO.info("[DISTRO-VERIFY-FAILED] IpPortBasedClient[{}] revision local={}, remote={}",
                        client.getClientId(), client.getRevision(), verifyData.getRevision());
            }
        }
        return false;
    }
    
    private static class ExpiredClientCleaner implements Runnable {
        
        private final EphemeralIpPortClientManager clientManager;
        
        private final SwitchDomain switchDomain;
        
        public ExpiredClientCleaner(EphemeralIpPortClientManager clientManager, SwitchDomain switchDomain) {
            this.clientManager = clientManager;
            this.switchDomain = switchDomain;
        }
        
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            for (String each : clientManager.allClientId()) {
                IpPortBasedClient client = (IpPortBasedClient) clientManager.getClient(each);
                if (null != client && isExpireClient(currentTime, client)) {
//                    clientManager.clientDisconnected(each);
                }
            }
        }
        
        private boolean isExpireClient(long currentTime, IpPortBasedClient client) {
            long noUpdatedTime = currentTime - client.getLastUpdatedTime();
            return client.isEphemeral() && (
                    isExpirePublishedClient(noUpdatedTime, client) && isExpireSubscriberClient(noUpdatedTime, client)
                            || noUpdatedTime > ClientConfig.getInstance().getClientExpiredTime());
        }
        
        private boolean isExpirePublishedClient(long noUpdatedTime, IpPortBasedClient client) {
            return client.getAllPublishedService().isEmpty() && noUpdatedTime > Constants.DEFAULT_IP_DELETE_TIMEOUT;
        }
        
        private boolean isExpireSubscriberClient(long noUpdatedTime, IpPortBasedClient client) {
            return client.getAllSubscribeService().isEmpty() || noUpdatedTime > switchDomain.getDefaultPushCacheMillis();
        }
    }
}
