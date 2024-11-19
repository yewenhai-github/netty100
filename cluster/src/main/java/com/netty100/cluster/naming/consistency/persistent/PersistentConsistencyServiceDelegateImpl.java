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

package com.netty100.cluster.naming.consistency.persistent;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.core.distributed.ProtocolManager;
import com.netty100.cluster.naming.consistency.Datum;
import com.netty100.cluster.naming.consistency.RecordListener;
import com.netty100.cluster.naming.consistency.persistent.impl.BasePersistentServiceProcessor;
import com.netty100.cluster.naming.consistency.persistent.impl.PersistentServiceProcessor;
import com.netty100.cluster.naming.consistency.persistent.impl.StandalonePersistentServiceProcessor;
import com.netty100.cluster.naming.pojo.Record;
import com.netty100.cluster.sys.env.EnvUtil;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Persistent consistency service delegate.
 *
 * @author why
 */
@DependsOn("ProtocolManager")
@Component("persistentConsistencyServiceDelegate")
public class PersistentConsistencyServiceDelegateImpl implements PersistentConsistencyService {
    
    private final BasePersistentServiceProcessor persistentServiceProcessor;
    
    public PersistentConsistencyServiceDelegateImpl(ProtocolManager protocolManager) throws Exception {
        this.persistentServiceProcessor = createPersistentServiceProcessor(protocolManager);
    }
    
    @Override
    public void put(String key, Record value) throws CapException {
        persistentServiceProcessor.put(key, value);
    }
    
    @Override
    public void remove(String key) throws CapException {
        persistentServiceProcessor.remove(key);
    }
    
    @Override
    public Datum get(String key) throws CapException {
        return persistentServiceProcessor.get(key);
    }
    
    @Override
    public void listen(String key, RecordListener listener) throws CapException {
        persistentServiceProcessor.listen(key, listener);
    }
    
    @Override
    public void unListen(String key, RecordListener listener) throws CapException {
        persistentServiceProcessor.unListen(key, listener);
    }
    
    @Override
    public boolean isAvailable() {
        return persistentServiceProcessor.isAvailable();
    }
    
    @Override
    public Optional<String> getErrorMsg() {
        return persistentServiceProcessor.getErrorMsg();
    }
    
    private BasePersistentServiceProcessor createPersistentServiceProcessor(ProtocolManager protocolManager)
            throws Exception {
        final BasePersistentServiceProcessor processor =
                EnvUtil.getStandaloneMode() ? new StandalonePersistentServiceProcessor()
                        : new PersistentServiceProcessor(protocolManager);
        processor.afterConstruct();
        return processor;
    }
}
