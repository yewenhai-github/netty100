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

package com.netty100.cluster.plugin.control.tps.cap;

import com.netty100.cluster.plugin.control.Loggers;
import com.netty100.cluster.plugin.control.tps.TpsBarrier;
import com.netty100.cluster.plugin.control.tps.request.BarrierCheckRequest;
import com.netty100.cluster.plugin.control.tps.request.TpsCheckRequest;
import com.netty100.cluster.plugin.control.tps.response.TpsCheckResponse;
import com.netty100.cluster.plugin.control.tps.rule.RuleDetail;
import com.netty100.cluster.plugin.control.tps.rule.TpsControlRule;

/**
 * tps barrier for tps point.
 *
 * @author why
 */
public class CapTpsBarrier extends TpsBarrier {
    
    public CapTpsBarrier(String pointName) {
        super(pointName);
    }
    
    /**
     * apply tps.
     *
     * @param tpsCheckRequest tpsCheckRequest.
     * @return check current tps is allowed.
     */
    public TpsCheckResponse applyTps(TpsCheckRequest tpsCheckRequest) {
        
        BarrierCheckRequest pointCheckRequest = new BarrierCheckRequest();
        pointCheckRequest.setCount(tpsCheckRequest.getCount());
        pointCheckRequest.setPointName(super.getPointName());
        pointCheckRequest.setTimestamp(tpsCheckRequest.getTimestamp());
        return super.getPointBarrier().applyTps(pointCheckRequest);
    }
    
    /**
     * apply rule.
     *
     * @param newControlRule newControlRule.
     */
    public synchronized void applyRule(TpsControlRule newControlRule) {
        Loggers.CONTROL.info("Apply tps control rule start,pointName=[{}]  ", this.getPointName());
        
        //1.reset all monitor point for null.
        if (newControlRule == null || newControlRule.getPointRule() == null) {
            Loggers.CONTROL.info("Clear all tps control rule ,pointName=[{}]  ", this.getPointName());
            super.getPointBarrier().clearLimitRule();
            return;
        }
        
        //2.check point rule.
        RuleDetail newPointRule = newControlRule.getPointRule();
        
        Loggers.CONTROL.info("Update  point  control rule ,pointName=[{}],original maxTps={}, new maxTps={}"
                        + ",original monitorType={}, original monitorType={}, ", this.getPointName(),
                this.pointBarrier.getMaxCount(), newPointRule.getMaxCount(), this.pointBarrier.getMonitorType(),
                newPointRule.getMonitorType());
        this.pointBarrier.applyRuleDetail(newPointRule);
        
        Loggers.CONTROL.info("Apply tps control rule end,pointName=[{}]  ", this.getPointName());
        
    }
}
