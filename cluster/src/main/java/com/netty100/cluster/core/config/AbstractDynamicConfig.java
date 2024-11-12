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

package com.netty100.cluster.core.config;

import com.netty100.cluster.common.event.ServerConfigChangeEvent;
import com.netty100.cluster.common.notify.Event;
import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.common.notify.listener.Subscriber;
import com.netty100.cluster.core.utils.Loggers;

/**
 * Cap abstract dynamic config.
 *
 * @author yewenhai
 */
public abstract class AbstractDynamicConfig extends Subscriber<ServerConfigChangeEvent> {
    
    private final String configName;
    
    protected AbstractDynamicConfig(String configName) {
        this.configName = configName;
        NotifyCenter.registerSubscriber(this);
    }
    
    @Override
    public void onEvent(ServerConfigChangeEvent event) {
        resetConfig();
    }
    
    @Override
    public Class<? extends Event> subscribeType() {
        return ServerConfigChangeEvent.class;
    }
    
    protected void resetConfig() {
        try {
            getConfigFromEnv();
            Loggers.CORE.info("Get {} config from env, {}", configName, printConfig());
        } catch (Exception e) {
            Loggers.CORE.warn("Upgrade {} config from env failed, will use old value", configName, e);
        }
    }
    
    /**
     * Execute get config from env actually.
     */
    protected abstract void getConfigFromEnv();
    
    /**
     * Print config content.
     *
     * @return config content
     */
    protected abstract String printConfig();
}
