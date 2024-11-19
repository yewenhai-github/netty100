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

package com.netty100.cluster.plugin.control;

import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.common.spi.CapServiceLoader;
import com.netty100.cluster.plugin.control.configs.ControlConfigs;
import com.netty100.cluster.plugin.control.connection.ConnectionControlManager;
import com.netty100.cluster.plugin.control.connection.cap.CapConnectionControlManager;
import com.netty100.cluster.plugin.control.event.ConnectionLimitRuleChangeEvent;
import com.netty100.cluster.plugin.control.event.TpsControlRuleChangeEvent;
import com.netty100.cluster.plugin.control.ruleactivator.RuleParser;
import com.netty100.cluster.plugin.control.ruleactivator.RuleParserProxy;
import com.netty100.cluster.plugin.control.ruleactivator.RuleStorageProxy;
import com.netty100.cluster.plugin.control.tps.TpsControlManager;
import com.netty100.cluster.plugin.control.tps.cap.CapTpsControlManager;

import java.util.Collection;

/**
 * control manager center.
 *
 * @author why
 */
public class ControlManagerCenter {
    
    static ControlManagerCenter instance = null;
    
    private TpsControlManager tpsControlManager;
    
    private ConnectionControlManager connectionControlManager;
    
    private RuleStorageProxy ruleStorageProxy;
    
    private void initConnectionManager() {
        
        Collection<ConnectionControlManager> connectionControlManagers = CapServiceLoader
                .load(ConnectionControlManager.class);
        String connectionManagerName = ControlConfigs.getInstance().getConnectionManager();
        
        for (ConnectionControlManager connectionControlManagerInternal : connectionControlManagers) {
            if (connectionControlManagerInternal.getName().equalsIgnoreCase(connectionManagerName)) {
                Loggers.CONTROL.info("Found  connection control manager of name={},class={}", connectionManagerName,
                        connectionControlManagerInternal.getClass().getSimpleName());
                connectionControlManager = connectionControlManagerInternal;
                break;
            }
        }
        if (connectionControlManager == null) {
            Loggers.CONTROL.warn("Fail to connection control manager of name ：" + connectionManagerName);
            connectionControlManager = new CapConnectionControlManager();
        }
        
    }
    
    private void initTpsControlManager() {
        
        Collection<TpsControlManager> tpsControlManagers = CapServiceLoader.load(TpsControlManager.class);
        String tpsManagerName = ControlConfigs.getInstance().getTpsManager();
        
        for (TpsControlManager tpsControlManagerInternal : tpsControlManagers) {
            if (tpsControlManagerInternal.getName().equalsIgnoreCase(tpsManagerName)) {
                Loggers.CONTROL.info("Found  tps control manager of name={},class={}", tpsManagerName,
                        tpsControlManagerInternal.getClass().getSimpleName());
                tpsControlManager = tpsControlManagerInternal;
                break;
            }
        }
        if (tpsControlManager == null) {
            Loggers.CONTROL.warn("Fail to found tps control manager of name ：" + tpsManagerName);
            tpsControlManager = new CapTpsControlManager();
        }
        
    }
    
    private ControlManagerCenter() {
        initTpsControlManager();
        initConnectionManager();
        ruleStorageProxy = new RuleStorageProxy();
    }
    
    public RuleStorageProxy getRuleStorageProxy() {
        return ruleStorageProxy;
    }
    
    public RuleParser getRuleParser() {
        return RuleParserProxy.getInstance();
    }
    
    public TpsControlManager getTpsControlManager() {
        return tpsControlManager;
    }
    
    public ConnectionControlManager getConnectionControlManager() {
        return connectionControlManager;
    }
    
    public static final ControlManagerCenter getInstance() {
        if (instance == null) {
            synchronized (ControlManagerCenter.class) {
                if (instance == null) {
                    instance = new ControlManagerCenter();
                }
            }
        }
        return instance;
    }
    
    public void reloadTpsControlRule(String pointName, boolean external) {
        NotifyCenter.publishEvent(new TpsControlRuleChangeEvent(pointName, external));
    }
    
    public void reloadConnectionControlRule(boolean external) {
        NotifyCenter.publishEvent(new ConnectionLimitRuleChangeEvent(external));
    }
    
}
