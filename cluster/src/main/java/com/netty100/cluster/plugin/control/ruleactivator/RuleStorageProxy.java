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

package com.netty100.cluster.plugin.control.ruleactivator;

import com.netty100.cluster.common.spi.CapServiceLoader;
import com.netty100.cluster.common.utils.StringUtils;
import com.netty100.cluster.plugin.control.Loggers;
import com.netty100.cluster.plugin.control.configs.ControlConfigs;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * rule storage proxy.
 *
 * @author yewenhai
 */
public class RuleStorageProxy {
    
    private static final Logger LOGGER = Loggers.CONTROL;
    
    private static final RuleStorageProxy INSTANCE = new RuleStorageProxy();
    
    private LocalDiskRuleStorage localDiskRuleStorage = null;
    
    private ExternalRuleStorage externalRuleStorage = null;
    
    ControlRuleChangeActivator controlRuleChangeActivator = null;
    
    public RuleStorageProxy() {
        
        Collection<ExternalRuleStorage> persistRuleActivators = CapServiceLoader.load(ExternalRuleStorage.class);
        String rulePersistActivator = ControlConfigs.getInstance().getRuleExternalStorage();
        
        for (ExternalRuleStorage persistRuleActivator : persistRuleActivators) {
            if (persistRuleActivator.getName().equalsIgnoreCase(rulePersistActivator)) {
                LOGGER.info("Found persist rule storage of name ：" + rulePersistActivator);
                externalRuleStorage = persistRuleActivator;
                break;
            }
        }
        if (externalRuleStorage == null && StringUtils.isNotBlank(rulePersistActivator)) {
            LOGGER.error("Fail to found persist rule storage of name ：" + rulePersistActivator);
        }
        
        //local disk storage.
        localDiskRuleStorage = new LocalDiskRuleStorage();
        if (StringUtils.isNotBlank(ControlConfigs.getInstance().getLocalRuleStorageBaseDir())) {
            localDiskRuleStorage.setLocalRruleBaseDir(ControlConfigs.getInstance().getLocalRuleStorageBaseDir());
        }
        
        controlRuleChangeActivator = new ControlRuleChangeActivator();
        
    }
    
    public RuleStorage getLocalDiskStorage() {
        return localDiskRuleStorage;
    }
    
    public RuleStorage getExternalStorage() {
        return externalRuleStorage;
    }
    
    public static final RuleStorageProxy getInstance() {
        return INSTANCE;
    }
}
