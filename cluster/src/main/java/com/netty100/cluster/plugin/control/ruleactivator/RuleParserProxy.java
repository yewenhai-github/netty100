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
import com.netty100.cluster.plugin.control.Loggers;
import com.netty100.cluster.plugin.control.configs.ControlConfigs;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * rule parser proxy.
 *
 * @author yewenhai
 */
public class RuleParserProxy {
    
    private static final Logger LOGGER = Loggers.CONTROL;
    
    private static RuleParser instance;
    
    static {
        Collection<RuleParser> ruleParsers = CapServiceLoader.load(RuleParser.class);
        String ruleParserName = ControlConfigs.getInstance().getRuleParser();
        
        for (RuleParser ruleParser : ruleParsers) {
            if (ruleParser.getName().equalsIgnoreCase(ruleParserName)) {
                LOGGER.info("Found  rule parser of name={},class={}", ruleParserName,
                        ruleParser.getClass().getSimpleName());
                instance = ruleParser;
                break;
            }
        }
        if (instance == null) {
            LOGGER.warn("Fail to rule parser of name ：" + ruleParserName);
            instance = new CapRuleParser();
        }
    }
    
    public static RuleParser getInstance() {
        return instance;
    }
}
