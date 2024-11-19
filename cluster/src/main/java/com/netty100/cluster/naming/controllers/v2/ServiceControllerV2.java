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
import com.netty100.cluster.api.common.Constants;
import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.exception.api.CapApiException;
import com.netty100.cluster.api.model.v2.ErrorCode;
import com.netty100.cluster.api.model.v2.Result;
import com.netty100.cluster.api.selector.Selector;
import com.netty100.cluster.common.notify.NotifyCenter;
import com.netty100.cluster.common.trace.event.naming.DeregisterServiceTraceEvent;
import com.netty100.cluster.common.trace.event.naming.RegisterServiceTraceEvent;
import com.netty100.cluster.common.utils.JacksonUtils;
import com.netty100.cluster.common.utils.StringUtils;
import com.netty100.cluster.naming.core.ServiceOperatorV2Impl;
import com.netty100.cluster.naming.core.v2.metadata.ServiceMetadata;
import com.netty100.cluster.naming.core.v2.pojo.Service;
import com.netty100.cluster.naming.misc.UtilsAndCommons;
import com.netty100.cluster.naming.model.form.ServiceForm;
import com.netty100.cluster.naming.pojo.ServiceDetailInfo;
import com.netty100.cluster.naming.pojo.ServiceNameView;
import com.netty100.cluster.naming.selector.NoneSelector;
import com.netty100.cluster.naming.selector.SelectorManager;
import com.netty100.cluster.naming.utils.ServiceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service operation controller.
 *
 * @author why
 */
@CapApi
@RestController
@RequestMapping(UtilsAndCommons.DEFAULT_NACOS_NAMING_CONTEXT_V2 + UtilsAndCommons.NACOS_NAMING_SERVICE_CONTEXT)
public class ServiceControllerV2 {
    
    private final ServiceOperatorV2Impl serviceOperatorV2;
    
    private final SelectorManager selectorManager;
    
    public ServiceControllerV2(ServiceOperatorV2Impl serviceOperatorV2, SelectorManager selectorManager) {
        this.serviceOperatorV2 = serviceOperatorV2;
        this.selectorManager = selectorManager;
    }
    
    /**
     * Create a new service. This API will create a persistence service.
     */
    @PostMapping()
    public Result<String> create(ServiceForm serviceForm) throws Exception {
        serviceForm.validate();
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setProtectThreshold(serviceForm.getProtectThreshold());
        serviceMetadata.setSelector(parseSelector(serviceForm.getSelector()));
        serviceMetadata.setExtendData(UtilsAndCommons.parseMetadata(serviceForm.getMetadata()));
        serviceMetadata.setEphemeral(serviceForm.getEphemeral());
        serviceOperatorV2.create(Service
                .newService(serviceForm.getNamespaceId(), serviceForm.getGroupName(), serviceForm.getServiceName(),
                        serviceForm.getEphemeral()), serviceMetadata);
        NotifyCenter.publishEvent(
                new RegisterServiceTraceEvent(System.currentTimeMillis(), serviceForm.getNamespaceId(),
                        serviceForm.getGroupName(), serviceForm.getServiceName()));
        return Result.success("ok");
    }
    
    /**
     * Remove service.
     */
    @DeleteMapping()
    public Result<String> remove(
            @RequestParam(value = "namespaceId", defaultValue = Constants.DEFAULT_NAMESPACE_ID) String namespaceId,
            @RequestParam("serviceName") String serviceName,
            @RequestParam(value = "groupName", defaultValue = Constants.DEFAULT_GROUP) String groupName)
            throws Exception {
        serviceOperatorV2.delete(Service.newService(namespaceId, groupName, serviceName));
        NotifyCenter.publishEvent(
                new DeregisterServiceTraceEvent(System.currentTimeMillis(), namespaceId, groupName, serviceName));
        return Result.success("ok");
    }
    
    /**
     * Get detail of service.
     */
    @GetMapping()
    public Result<ServiceDetailInfo> detail(
            @RequestParam(value = "namespaceId", defaultValue = Constants.DEFAULT_NAMESPACE_ID) String namespaceId,
            @RequestParam("serviceName") String serviceName,
            @RequestParam(value = "groupName", defaultValue = Constants.DEFAULT_GROUP) String groupName)
            throws Exception {
        ServiceDetailInfo result = serviceOperatorV2
                .queryService(Service.newService(namespaceId, groupName, serviceName));
        return Result.success(result);
    }
    
    /**
     * List all service names.
     */
    @GetMapping("/list")
    public Result<ServiceNameView> list(
            @RequestParam(value = "namespaceId", required = false, defaultValue = Constants.DEFAULT_NAMESPACE_ID) String namespaceId,
            @RequestParam(value = "groupName", required = false, defaultValue = Constants.DEFAULT_GROUP) String groupName,
            @RequestParam(value = "selector", required = false, defaultValue = StringUtils.EMPTY) String selector,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize)
            throws Exception {
        pageSize = Math.min(500, pageSize);
        ServiceNameView result = new ServiceNameView();
        Collection<String> serviceNameList = serviceOperatorV2.listService(namespaceId, groupName, selector);
        result.setCount(serviceNameList.size());
        result.setServices(ServiceUtil.pageServiceName(pageNo, pageSize, serviceNameList));
        return Result.success(result);
    }
    
    /**
     * Update service.
     */
    @PutMapping()
    public Result<String> update(ServiceForm serviceForm) throws Exception {
        serviceForm.validate();
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setProtectThreshold(serviceForm.getProtectThreshold());
        serviceMetadata.setExtendData(UtilsAndCommons.parseMetadata(serviceForm.getMetadata()));
        serviceMetadata.setSelector(parseSelector(serviceForm.getSelector()));
        Service service = Service
                .newService(serviceForm.getNamespaceId(), serviceForm.getGroupName(), serviceForm.getServiceName());
        serviceOperatorV2.update(service, serviceMetadata);
        return Result.success("ok");
    }
    
    private Selector parseSelector(String selectorJsonString) throws Exception {
        if (StringUtils.isBlank(selectorJsonString)) {
            return new NoneSelector();
        }
        
        JsonNode selectorJson = JacksonUtils.toObj(URLDecoder.decode(selectorJsonString, "UTF-8"));
        String type = Optional.ofNullable(selectorJson.get("type")).orElseThrow(
                () -> new CapApiException(CapException.INVALID_PARAM, ErrorCode.SELECTOR_ERROR,
                        "not match any type of selector!")).asText();
        String expression = Optional.ofNullable(selectorJson.get("expression")).map(JsonNode::asText).orElse(null);
        Selector selector = selectorManager.parseSelector(type, expression);
        if (Objects.isNull(selector)) {
            throw new CapApiException(CapException.INVALID_PARAM, ErrorCode.SELECTOR_ERROR,
                    "not match any type of selector!");
        }
        return selector;
    }
}
