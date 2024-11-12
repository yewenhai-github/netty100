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

package com.netty100.cluster.naming.core;

import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.core.cluster.MemberChangeListener;
import com.netty100.cluster.core.cluster.MemberUtil;
import com.netty100.cluster.core.cluster.MembersChangeEvent;
import com.netty100.cluster.core.cluster.NodeState;
import com.netty100.cluster.core.cluster.ServerMemberManager;
import com.netty100.cluster.naming.misc.Loggers;
import com.netty100.cluster.naming.misc.SwitchDomain;
import com.netty100.cluster.sys.env.EnvUtil;
import com.netty100.common.utils.SysUtility;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Distro mapper, judge which server response input service.
 *
 * @author yewenhai
 */
@Component("distroMapper")
public class DistroMapper extends MemberChangeListener {
    
    /**
     * List of service nodes, you must ensure that the order of healthyList is the same for all nodes.
     */
    private volatile List<String> healthyList = new ArrayList<>();
    
    private final SwitchDomain switchDomain;
    
    private final ServerMemberManager memberManager;
    
    public DistroMapper(ServerMemberManager memberManager, SwitchDomain switchDomain) {
        this.memberManager = memberManager;
        this.switchDomain = switchDomain;
    }
    
    public List<String> getHealthyList() {
        return healthyList;
    }
    
    /**
     * init server list.
     */
    @PostConstruct
    public void init() {
        NotifyCenter.registerSubscriber(this);
        this.healthyList = MemberUtil.simpleMembers(memberManager.allMembers());
    }
    
    /**
     * Judge whether current server is responsible for input tag.
     *
     * @param responsibleTag responsible tag, serviceName for v1 and ip:port for v2
     * @return true if input service is response, otherwise false
     */
    public boolean responsible(String responsibleTag) {
        final List<String> servers = healthyList;
        
        if (!switchDomain.isDistroEnabled() || EnvUtil.getStandaloneMode()) {
            return true;
        }
        
        if (CollectionUtils.isEmpty(servers)) {
            // means distro config is not ready yet
            return false;
        }
        //当前地址不在健康的集成中，直接返回true
        String localAddress = EnvUtil.getLocalAddress();
        int index = servers.indexOf(localAddress);
        int lastIndex = servers.lastIndexOf(localAddress);
        if (lastIndex < 0 || index < 0) {
            return true;
        }
//        int target = distroHash(responsibleTag) % servers.size();
//        return target >= index && target <= lastIndex;
        //直接判断当时生成的Client对象内网地址与当前服务器的地址是否一致
        return responsibleTag.equals(SysUtility.getHostIp());
    }
    
    /**
     * Calculate which other server response input tag.
     *
     * @param responsibleTag responsible tag, serviceName for v1 and ip:port for v2
     * @return server which response input service
     */
    public String mapSrv(String responsibleTag) {
        final List<String> servers = healthyList;
        
        if (CollectionUtils.isEmpty(servers) || !switchDomain.isDistroEnabled()) {
            return EnvUtil.getLocalAddress();
        }
        
        try {
            //可以看出，distro协议判断服务的负责节点采用简单的hash算法，如果nacos某节点宕机，则所有的服务都会重新计算映射到新的节点，变动较大。如果能采用一致性hash算法，则单节点宕机，只转移该故障节点负责的服务。
            int index = distroHash(responsibleTag) % servers.size();
            return servers.get(index);
        } catch (Throwable e) {
            Loggers.SRV_LOG
                    .warn("[CAP-DISTRO] distro mapper failed, return localhost: " + EnvUtil.getLocalAddress(), e);
            return EnvUtil.getLocalAddress();
        }
    }
    
    private int distroHash(String responsibleTag) {
        return Math.abs(responsibleTag.hashCode() % Integer.MAX_VALUE);
    }
    
    @Override
    public void onEvent(MembersChangeEvent event) {
        // Here, the node list must be sorted to ensure that all cap-server's
        // node list is in the same order
        List<String> list = MemberUtil.simpleMembers(MemberUtil.selectTargetMembers(event.getMembers(),
                member -> NodeState.UP.equals(member.getState()) || NodeState.SUSPICIOUS.equals(member.getState())));
        Collections.sort(list);
        Collection<String> old = healthyList;
        healthyList = Collections.unmodifiableList(list);
        Loggers.SRV_LOG.info("[CAP-DISTRO] healthy server list changed, old: {}, new: {}", old, healthyList);
    }
    
    @Override
    public boolean ignoreExpireEvent() {
        return true;
    }
}
