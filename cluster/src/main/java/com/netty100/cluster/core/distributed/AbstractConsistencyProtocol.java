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

import com.netty100.cluster.consistency.Config;
import com.netty100.cluster.consistency.ConsistencyProtocol;
import com.netty100.cluster.consistency.RequestProcessor;
import com.netty100.cluster.consistency.ProtocolMetaData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consistent protocol base class.
 *
 * @author why
 */
@SuppressWarnings("all")
public abstract class AbstractConsistencyProtocol<T extends Config, L extends RequestProcessor>
        implements ConsistencyProtocol<T, L> {
    
    protected final ProtocolMetaData metaData = new ProtocolMetaData();
    
    protected Map<String, L> processorMap = Collections.synchronizedMap(new HashMap<>());
    
    public void loadLogProcessor(List<L> logProcessors) {
        logProcessors.forEach(logDispatcher -> processorMap.put(logDispatcher.group(), logDispatcher));
    }
    
    protected Map<String, L> allProcessor() {
        return processorMap;
    }
    
    @Override
    public ProtocolMetaData protocolMetaData() {
        return this.metaData;
    }
    
}
