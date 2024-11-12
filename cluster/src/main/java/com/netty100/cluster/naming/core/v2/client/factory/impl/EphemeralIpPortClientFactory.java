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

package com.netty100.cluster.naming.core.v2.client.factory.impl;

import com.netty100.cluster.naming.constants.ClientConstants;
import com.netty100.cluster.naming.core.v2.client.ClientAttributes;
import com.netty100.cluster.naming.core.v2.client.factory.ClientFactory;
import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import io.netty.channel.Channel;

/**
 * Client factory for ephemeral {@link IpPortBasedClient}.
 *
 * @author yewenhai
 */
public class EphemeralIpPortClientFactory implements ClientFactory<IpPortBasedClient> {
    
    @Override
    public String getType() {
        return ClientConstants.EPHEMERAL_IP_PORT;
    }
    
    @Override
    public IpPortBasedClient newClient(String clientId, ClientAttributes attributes) {
        long revision = attributes.getClientAttribute(ClientConstants.REVISION, 0);
        String intranetip = attributes.getClientAttribute(ClientConstants.INTRANETIP, "");
        Channel channel = attributes.getClientAttribute(ClientConstants.CHANNEL, null);

        IpPortBasedClient ipPortBasedClient = new IpPortBasedClient(clientId, true, revision);
        ipPortBasedClient.setChannel(channel);
        ipPortBasedClient.setIntranetIp(intranetip);
        ipPortBasedClient.setAttributes(attributes);
        ipPortBasedClient.setResponsibleId(intranetip);
        return ipPortBasedClient;
    }

    //数据初始化用，不需要同步channel
    @Override
    public IpPortBasedClient newSyncedClient(String clientId, ClientAttributes attributes) {
        long revision = attributes.getClientAttribute(ClientConstants.REVISION, 0);

        IpPortBasedClient ipPortBasedClient = new IpPortBasedClient(clientId, true, revision);
        ipPortBasedClient.setAttributes(attributes);
        ipPortBasedClient.setIntranetIp("");
        ipPortBasedClient.setChannel(null);
        return ipPortBasedClient;
    }
}
