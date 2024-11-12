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

package com.netty100.cluster.core.cluster;

import com.netty100.cluster.api.exception.CapException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Member node addressing mode.
 *
 * @author yewenhai
 */
public interface MemberLookup {
    
    /**
     * start.
     *
     * @throws CapException NacosException
     */
    void start() throws CapException;
    
    /**
     * is using address server.
     *
     * @return using address server or not.
     */
    boolean useKernelServer();
    
    /**
     * Inject the ServerMemberManager property.
     *
     * @param memberManager {@link ServerMemberManager}
     */
    void injectMemberManager(ServerMemberManager memberManager);
    
    /**
     * The addressing pattern finds cluster nodes.
     *
     * @param members {@link Collection}
     */
    void afterLookup(Collection<Member> members);
    
    /**
     * Addressing mode closed.
     *
     * @throws CapException NacosException
     */
    void destroy() throws CapException;
    
    /**
     * Some data information about the addressing pattern.
     *
     * @return {@link Map}
     */
    default Map<String, Object> info() {
        return Collections.emptyMap();
    }
    
}
