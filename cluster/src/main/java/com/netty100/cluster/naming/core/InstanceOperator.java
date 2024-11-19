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

package com.netty100.cluster.naming.core;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.api.naming.pojo.Instance;
import com.netty100.cluster.api.naming.pojo.ServiceInfo;
import com.netty100.cluster.naming.healthcheck.RsInfo;
import com.netty100.cluster.naming.pojo.InstanceOperationInfo;
import com.netty100.cluster.naming.pojo.Subscriber;
import com.netty100.cluster.naming.pojo.instance.BeatInfoInstanceBuilder;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;

/**
 * Instance operator.
 *
 * @author why
 */
public interface InstanceOperator {
    
    /**
     * Register an instance to a service in AP mode.
     *
     * @param namespaceId id of namespace
     * @param serviceName grouped service name group@@service
     * @param instance    instance to register
     * @throws CapException cap exception when register failed
     */
    void registerInstance(String namespaceId, String serviceName, Instance instance, String clientId, ChannelHandlerContext ctx) throws CapException;
    
    /**
     * Remove instance from service.
     *
     * @param namespaceId namespace
     * @param serviceName grouped service name group@@service
     * @param instance    instance
     * @throws CapException cap exception when remove failed
     */
    void removeInstance(String namespaceId, String serviceName, Instance instance, String clientId) throws CapException;
    
    /**
     * Update instance information. Due to the basic information can't be changed, so this update should only update
     * metadata.
     *
     * <p>Update API will replace the whole metadata with new input instance.
     *
     * @param namespaceId namespace
     * @param serviceName grouped service name group@@service
     * @param instance    instance
     * @throws CapException cap exception when update failed
     */
    void updateInstance(String namespaceId, String serviceName, Instance instance) throws CapException;
    
    /**
     * Patch update instance information. Due to the basic information can't be changed, so this update should only
     * update metadata.
     *
     * <p>Patch update will only update variables in requests, the others will keep original value.
     *
     * @param namespaceId namespace
     * @param serviceName grouped service name group@@service
     * @param patchObject objects need to be patch
     * @throws CapException cap exception when update failed
     */
    void patchInstance(String namespaceId, String serviceName, InstancePatchObject patchObject) throws CapException;
    
    /**
     * Get all instance of input service.
     *
     * @param namespaceId namespace
     * @param serviceName grouped service name group@@service
     * @param subscriber  subscriber info
     * @param cluster     cluster of instances
     * @param healthOnly  whether only return health instances
     * @return service info
     * @throws Exception exception when list instance failed
     */
    ServiceInfo listInstance(String namespaceId, String serviceName, Subscriber subscriber, String cluster,
                             boolean healthOnly) throws Exception;
    
    /**
     * Get instance detail information.
     *
     * @param namespaceId namespace
     * @param serviceName grouped service name group@@service
     * @param cluster     cluster of instance
     * @param ip          ip of instance
     * @param port        port of instance
     * @return instance info
     * @throws CapException cap exception during query
     */
    Instance getInstance(String namespaceId, String serviceName, String cluster, String ip, int port)
            throws CapException;
    
    /**
     * Handle beat request.
     *
     * @param namespaceId namespace
     * @param serviceName grouped service name group@@service
     * @param ip          ip of instance
     * @param port        port of instance
     * @param cluster     cluster of instance
     * @param clientBeat  client beat info
     * @param builder     client beat instance builder, will be used when current instance is not exist and clientBeat
     *                    exist
     * @return result code
     * @throws CapException cap exception when service non-exist and client beat info is null
     */
    int handleBeat(String namespaceId, String serviceName, String ip, int port, String cluster, RsInfo clientBeat,
            BeatInfoInstanceBuilder builder) throws CapException;
    
    /**
     * Get heart beat interval for specified instance.
     *
     * @param namespaceId namespace
     * @param serviceName grouped service name group@@service
     * @param ip          ip of instance
     * @param port        port of instance
     * @param cluster     cluster of instance
     * @return heart beat interval
     */
    long getHeartBeatInterval(String namespaceId, String serviceName, String ip, int port, String cluster);
    
    /**
     * List all instances whatever status they are.
     *
     * @param namespaceId namespace
     * @param serviceName grouped service name group@@service
     * @return all instances
     * @throws CapException cap exception during query
     */
    List<? extends Instance> listAllInstances(String namespaceId, String serviceName) throws CapException;
    
    /**
     * Batch update metadata of instances.
     *
     * @param namespaceId           namespace Id of instances
     * @param instanceOperationInfo instance operation info
     * @param metadata              updated metadata
     * @return updated instance
     * @throws CapException cap exception during update
     */
    List<String> batchUpdateMetadata(String namespaceId, InstanceOperationInfo instanceOperationInfo,
            Map<String, String> metadata) throws CapException;
    
    /**
     * Batch delete metadata of instances.
     *
     * @param namespaceId           namespace Id of instances
     * @param instanceOperationInfo instance operation info
     * @param metadata              delete metadata
     * @return updated instance
     * @throws CapException cap exception during update
     */
    List<String> batchDeleteMetadata(String namespaceId, InstanceOperationInfo instanceOperationInfo,
            Map<String, String> metadata) throws CapException;
}
