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

package com.netty100.cluster.naming.core;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.naming.core.v2.metadata.ServiceMetadata;
import com.netty100.cluster.naming.core.v2.pojo.Service;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

/**
 * Service operator.
 *
 * @author yewenhai
 */
public interface ServiceOperator {
    
    /**
     * Create new service.
     *
     * @param namespaceId namespace id of service
     * @param serviceName grouped service name format like 'groupName@@serviceName'
     * @param metadata    new metadata of service
     * @throws CapException cap exception during creating
     */
    void create(String namespaceId, String serviceName, ServiceMetadata metadata) throws CapException;
    
    /**
     * Update service information. Due to service basic information can't be changed, so update should only update the
     * metadata of service.
     *
     * @param service  service need to be updated.
     * @param metadata new metadata of service.
     * @throws CapException cap exception during update
     */
    void update(Service service, ServiceMetadata metadata) throws CapException;
    
    /**
     * Delete service.
     *
     * @param namespaceId namespace id of service
     * @param serviceName grouped service name format like 'groupName@@serviceName'
     * @throws CapException cap exception during delete
     */
    void delete(String namespaceId, String serviceName) throws CapException;
    
    /**
     * Query service detail.
     *
     * @param namespaceId namespace id of service
     * @param serviceName grouped service name format like 'groupName@@serviceName'
     * @return service detail with cluster info
     * @throws CapException cap exception during query
     */
    ObjectNode queryService(String namespaceId, String serviceName) throws CapException;
    
    /**
     * Page list service name.
     *
     * @param namespaceId namespace id of services
     * @param groupName   group name of services
     * @param selector    selector
     * @return services name list
     * @throws CapException cap exception during query
     */
    Collection<String> listService(String namespaceId, String groupName, String selector) throws CapException;
    
    /**
     * list All service namespace.
     *
     * @return all namespace
     */
    Collection<String> listAllNamespace();
    
    /**
     * Search service name in namespace according to expr.
     *
     * @param namespaceId namespace id
     * @param expr        search expr
     * @return service name collection of match expr
     * @throws CapException cap exception during query
     */
    Collection<String> searchServiceName(String namespaceId, String expr) throws CapException;
}
