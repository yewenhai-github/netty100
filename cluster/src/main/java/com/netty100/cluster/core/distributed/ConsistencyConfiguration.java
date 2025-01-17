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

package com.netty100.cluster.core.distributed;

import com.netty100.cluster.core.distributed.raft.JRaftProtocol;
import com.netty100.cluster.common.spi.CapServiceLoader;
import com.netty100.cluster.consistency.cp.CPProtocol;
import com.netty100.cluster.core.cluster.ServerMemberManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * consistency configuration.
 *
 * @author why
 */
@Configuration
public class ConsistencyConfiguration {
    
    @Bean(value = "strongAgreementProtocol")
    public CPProtocol strongAgreementProtocol(ServerMemberManager memberManager) throws Exception {
        final CPProtocol protocol = getProtocol(CPProtocol.class, () -> new JRaftProtocol(memberManager));
        return protocol;
    }
    
    private <T> T getProtocol(Class<T> cls, Callable<T> builder) throws Exception {
        Collection<T> protocols = CapServiceLoader.load(cls);
        
        // Select only the first implementation
        
        Iterator<T> iterator = protocols.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return builder.call();
        }
    }
    
}
