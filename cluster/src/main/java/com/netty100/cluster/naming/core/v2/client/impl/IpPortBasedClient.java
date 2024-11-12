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

package com.netty100.cluster.naming.core.v2.client.impl;

import com.netty100.cluster.naming.core.v2.client.AbstractClient;
import com.netty100.cluster.naming.core.v2.pojo.HealthCheckInstancePublishInfo;
import com.netty100.cluster.naming.core.v2.pojo.InstancePublishInfo;
import com.netty100.cluster.naming.core.v2.pojo.Service;
import com.netty100.cluster.naming.healthcheck.HealthCheckReactor;
import com.netty100.cluster.naming.healthcheck.heartbeat.ClientBeatCheckTaskV2;
import com.netty100.cluster.naming.healthcheck.v2.HealthCheckTaskV2;
import com.netty100.cluster.naming.misc.ClientConfig;
import io.netty.channel.Channel;
//import com.netty100.cluster.naming.monitor.MetricsMonitor;

import java.util.Collection;

/**
 * Cap naming client based ip and port.
 *
 * <p>The client is bind to the ip and port users registered. It's a abstract content to simulate the tcp session
 * client.
 *
 * @author yewenhai
 */
public class IpPortBasedClient extends AbstractClient {
    
    public static final String ID_DELIMITER = "_";
    private final String clientId;

    private Channel channel;

    private String intranetIp;

    private final boolean ephemeral;
    
    private String responsibleId;
    
    private ClientBeatCheckTaskV2 beatCheckTask;
    
    private HealthCheckTaskV2 healthCheckTaskV2;
    
    public IpPortBasedClient(String clientId, boolean ephemeral) {
        this(clientId, ephemeral, null);
    }
    
    public IpPortBasedClient(String clientId, boolean ephemeral, Long revision) {
        super(revision);
        this.ephemeral = ephemeral;
        this.clientId = clientId;
    }

    public static String getClientId(String address, boolean ephemeral) {
        return address + ID_DELIMITER + ephemeral;
    }
    
    @Override
    public String getClientId() {
        return clientId;
    }
    
    @Override
    public boolean isEphemeral() {
        return ephemeral;
    }

    public String getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(String responsibleId) {
        this.responsibleId = responsibleId;
    }

    @Override
    public boolean addServiceInstance(Service service, InstancePublishInfo instancePublishInfo) {
        return super.addServiceInstance(service, parseToHealthCheckInstance(instancePublishInfo));
    }
    
    @Override
    public boolean isExpire(long currentTime) {
        return isEphemeral() && getAllPublishedService().isEmpty() && currentTime - getLastUpdatedTime() > ClientConfig
                .getInstance().getClientExpiredTime();
    }
    
    public Collection<InstancePublishInfo> getAllInstancePublishInfo() {
        return publishers.values();
    }
    
    @Override
    public void release() {
        super.release();
        if (ephemeral) {
            HealthCheckReactor.cancelCheck(beatCheckTask);
        } else {
            healthCheckTaskV2.setCancelled(true);
        }
    }
    
    private HealthCheckInstancePublishInfo parseToHealthCheckInstance(InstancePublishInfo instancePublishInfo) {
        HealthCheckInstancePublishInfo result;
        if (instancePublishInfo instanceof HealthCheckInstancePublishInfo) {
            result = (HealthCheckInstancePublishInfo) instancePublishInfo;
        } else {
            result = new HealthCheckInstancePublishInfo();
            result.setIp(instancePublishInfo.getIp());
            result.setPort(instancePublishInfo.getPort());
            result.setHealthy(instancePublishInfo.isHealthy());
            result.setCluster(instancePublishInfo.getCluster());
            result.setExtendDatum(instancePublishInfo.getExtendDatum());
        }
        if (!ephemeral) {
            result.initHealthCheck();
        }
        return result;
    }
    
    /**
     * Init client.
     */
    public void init() {
        if (ephemeral) {
            beatCheckTask = new ClientBeatCheckTaskV2(this);
            HealthCheckReactor.scheduleCheck(beatCheckTask);
        } else {
            healthCheckTaskV2 = new HealthCheckTaskV2(this);
            HealthCheckReactor.scheduleCheck(healthCheckTaskV2);
        }
    }
    
    /**
     * Purely put instance into service without publish events.
     */
    public void putServiceInstance(Service service, InstancePublishInfo instance) {
        if (null == publishers.put(service, parseToHealthCheckInstance(instance))) {
//            MetricsMonitor.incrementInstanceCount();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String getIntranetIp() {
        return intranetIp;
    }

    public void setIntranetIp(String intranetIp) {
        this.intranetIp = intranetIp;
    }
}
