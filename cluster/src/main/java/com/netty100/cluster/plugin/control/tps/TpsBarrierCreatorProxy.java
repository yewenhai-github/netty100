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

package com.netty100.cluster.plugin.control.tps;

import com.netty100.cluster.common.spi.CapServiceLoader;
import com.netty100.cluster.plugin.control.Loggers;
import com.netty100.cluster.plugin.control.configs.ControlConfigs;
import com.netty100.cluster.plugin.control.tps.cap.DefaultCapTpsBarrierCreator;

import java.util.Collection;

/**
 * tps barrier creator proxy.
 *
 * @author why
 */
public class TpsBarrierCreatorProxy {
    
    static TpsBarrierCreator tpsBarrierCreator;
    
    static {
        String tpsRuleBarrierCreator = null;
        try {
            tpsRuleBarrierCreator = ControlConfigs.getInstance().getTpsRuleBarrierCreator();
            Collection<TpsBarrierCreator> loadedCreators = CapServiceLoader.load(TpsBarrierCreator.class);
            for (TpsBarrierCreator barrierCreator : loadedCreators) {
                if (tpsRuleBarrierCreator.equalsIgnoreCase(barrierCreator.getName())) {
                    Loggers.CONTROL.info("Found tps barrier creator of name : {}", tpsRuleBarrierCreator);
                    tpsBarrierCreator = barrierCreator;
                    break;
                }
            }
        } catch (Throwable throwable) {
            Loggers.CONTROL.warn("Fail to load tpsRuleBarrierCreator ", throwable);
        }
        if (tpsBarrierCreator == null) {
            Loggers.CONTROL.warn("Fail to found tps barrier creator of name : {},use  default local simple creator",
                    tpsRuleBarrierCreator);
            tpsBarrierCreator = new DefaultCapTpsBarrierCreator();
        }
    }
    
    public static TpsBarrierCreator getTpsBarrierCreator() {
        return tpsBarrierCreator;
    }
}
