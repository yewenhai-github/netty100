/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
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

package com.netty100.cluster.naming.controllers.v2;

import com.netty100.cluster.api.annotation.CapApi;
import com.netty100.cluster.api.exception.api.CapApiException;
import com.netty100.cluster.api.model.v2.ErrorCode;
import com.netty100.cluster.api.model.v2.Result;
import com.netty100.cluster.naming.cluster.ServerStatusManager;
import com.netty100.cluster.naming.constants.ClientConstants;
import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import com.netty100.cluster.naming.core.v2.client.manager.ClientManager;
import com.netty100.cluster.naming.misc.SwitchDomain;
import com.netty100.cluster.naming.misc.SwitchManager;
import com.netty100.cluster.naming.misc.UtilsAndCommons;
import com.netty100.cluster.naming.model.form.UpdateSwitchForm;
import com.netty100.cluster.naming.model.vo.MetricsInfoVo;
import com.netty100.cluster.sys.env.EnvUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * OperatorControllerV2.
 *
 * @author why
 * @date 2022/9/8
 */
@CapApi
@RestController
@RequestMapping({UtilsAndCommons.DEFAULT_NACOS_NAMING_CONTEXT_V2 + UtilsAndCommons.NACOS_NAMING_OPERATOR_CONTEXT,
        UtilsAndCommons.DEFAULT_NACOS_NAMING_CONTEXT_V2 + "/ops"})
public class OperatorControllerV2 {
    
    private final SwitchManager switchManager;
    
    private final ServerStatusManager serverStatusManager;
    
    private final SwitchDomain switchDomain;
    
    private final ClientManager clientManager;
    
    public OperatorControllerV2(SwitchManager switchManager, ServerStatusManager serverStatusManager,
            SwitchDomain switchDomain, ClientManager clientManager) {
        this.switchManager = switchManager;
        this.serverStatusManager = serverStatusManager;
        this.switchDomain = switchDomain;
        this.clientManager = clientManager;
    }
    
    /**
     * Get switch information.
     *
     * @return switchDomain
     */
    @GetMapping("/switches")
    public Result<SwitchDomain> switches() {
        return Result.success(switchDomain);
    }
    
    /**
     * Update switch information.
     *
     * @param updateSwitchForm debug, entry, value
     * @return 'ok' if success
     * @throws Exception exception
     */
    @PutMapping("/switches")
    public Result<String> updateSwitch(UpdateSwitchForm updateSwitchForm) throws Exception {
        updateSwitchForm.validate();
        try {
            switchManager.update(updateSwitchForm.getEntry(), updateSwitchForm.getValue(), updateSwitchForm.getDebug());
        } catch (IllegalArgumentException e) {
            throw new CapApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.SERVER_ERROR,
                    e.getMessage());
        }
        
        return Result.success("ok");
    }
    
    /**
     * Get metrics information.
     *
     * @param onlyStatus onlyStatus
     * @return metrics information
     */
    @GetMapping("/metrics")
    public Result<MetricsInfoVo> metrics(
            @RequestParam(value = "onlyStatus", required = false, defaultValue = "true") Boolean onlyStatus) {
        MetricsInfoVo metricsInfoVo = new MetricsInfoVo();
        metricsInfoVo.setStatus(serverStatusManager.getServerStatus().name());
        if (onlyStatus) {
            return Result.success(metricsInfoVo);
        }
        
        int connectionBasedClient = 0;
        int ephemeralIpPortClient = 0;
        int persistentIpPortClient = 0;
        int responsibleClientCount = 0;
        Collection<String> allClientId = clientManager.allClientId();
        for (String clientId : allClientId) {
            if (clientId.contains(IpPortBasedClient.ID_DELIMITER)) {
                if (clientId.endsWith(ClientConstants.PERSISTENT_SUFFIX)) {
                    persistentIpPortClient += 1;
                } else {
                    ephemeralIpPortClient += 1;
                }
            } else {
                connectionBasedClient += 1;
            }
            if (clientManager.isResponsibleClient(clientManager.getClient(clientId))) {
                responsibleClientCount += 1;
            }
        }
        
//        metricsInfoVo.setServiceCount(MetricsMonitor.getDomCountMonitor().get());
//        metricsInfoVo.setInstanceCount(MetricsMonitor.getIpCountMonitor().get());
//        metricsInfoVo.setSubscribeCount(MetricsMonitor.getSubscriberCount().get());
        metricsInfoVo.setClientCount(allClientId.size());
        metricsInfoVo.setConnectionBasedClientCount(connectionBasedClient);
        metricsInfoVo.setEphemeralIpPortClientCount(ephemeralIpPortClient);
        metricsInfoVo.setPersistentIpPortClientCount(persistentIpPortClient);
        metricsInfoVo.setResponsibleClientCount(responsibleClientCount);
        metricsInfoVo.setCpu(EnvUtil.getCpu());
        metricsInfoVo.setLoad(EnvUtil.getLoad());
        metricsInfoVo.setMem(EnvUtil.getMem());
        return Result.success(metricsInfoVo);
    }
}
