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

package com.netty100.cluster.common.remote.client;

import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.common.remote.client.grpc.GrpcClusterClient;
import com.netty100.cluster.common.remote.client.grpc.GrpcSdkClient;
import com.netty100.cluster.common.remote.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RpcClientFactory.to support multi client for different modules of usage.
 *
 * @author why
 * @version $Id: RpcClientFactory.java, v 0.1 2020年07月14日 3:41 PM liuzunfei Exp $
 */
public class RpcClientFactory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("com.netty100.cluster.common.remote.client");
    
    private static final Map<String, RpcClient> CLIENT_MAP = new ConcurrentHashMap<>();
    
    /**
     * get all client.
     *
     * @return client collection.
     */
    public static Set<Map.Entry<String, RpcClient>> getAllClientEntries() {
        return CLIENT_MAP.entrySet();
    }
    
    /**
     * shut down client.
     *
     * @param clientName client name.
     */
    public static void destroyClient(String clientName) throws CapException {
        RpcClient rpcClient = CLIENT_MAP.remove(clientName);
        if (rpcClient != null) {
            rpcClient.shutdown();
        }
    }
    
    public static RpcClient getClient(String clientName) {
        return CLIENT_MAP.get(clientName);
    }
    
    /**
     * create a rpc client.
     *
     * @param clientName     client name.
     * @param connectionType client type.
     * @return rpc client.
     */
    public static RpcClient createClient(String clientName, ConnectionType connectionType, Map<String, String> labels) {
        return createClient(clientName, connectionType, null, null, labels);
    }

    public static RpcClient createClient(String clientName, ConnectionType connectionType, Map<String, String> labels,
                                         RpcClientTlsConfig tlsConfig) {
        return createClient(clientName, connectionType, null, null, labels, tlsConfig);

    }

    public static RpcClient createClient(String clientName, ConnectionType connectionType, Integer
            threadPoolCoreSize,
                                         Integer threadPoolMaxSize, Map<String, String> labels) {
        return createClient(clientName, connectionType, threadPoolCoreSize, threadPoolMaxSize, labels, null);
    }

    /**
     * create a rpc client.
     *
     * @param clientName         client name.
     * @param connectionType     client type.
     * @param threadPoolCoreSize grpc thread pool core size
     * @param threadPoolMaxSize  grpc thread pool max size
     * @param tlsConfig          tlsconfig
     * @return rpc client.
     */
    public static RpcClient createClient(String clientName, ConnectionType connectionType, Integer threadPoolCoreSize,
                                         Integer threadPoolMaxSize, Map<String, String> labels, RpcClientTlsConfig tlsConfig) {

        if (!ConnectionType.GRPC.equals(connectionType)) {
            throw new UnsupportedOperationException("unsupported connection type :" + connectionType.getType());
        }
        
        return CLIENT_MAP.computeIfAbsent(clientName, clientNameInner -> {
            LOGGER.info("[RpcClientFactory] create a new rpc client of " + clientName);
            try {
                return new GrpcSdkClient(clientNameInner, threadPoolCoreSize, threadPoolMaxSize, labels, tlsConfig);
            } catch (Throwable throwable) {
                LOGGER.error("Error to init GrpcSdkClient for client name :" + clientName, throwable);
                throw throwable;
            }
            
        });
    }
    
    /**
     * create a rpc client.
     *
     * @param clientName     client name.
     * @param connectionType client type.
     * @return rpc client.
     */
    public static RpcClient createClusterClient(String clientName, ConnectionType connectionType,
                                                Map<String, String> labels) {
        return createClusterClient(clientName, connectionType, null, null, labels);
    }

    public static RpcClient createClusterClient(String clientName, ConnectionType connectionType,
                                                Map<String, String> labels, RpcClientTlsConfig tlsConfig) {
        return createClusterClient(clientName, connectionType, null, null, labels, tlsConfig);
    }

    /**
     * create a rpc client.
     *
     * @param clientName         client name.
     * @param connectionType     client type.
     * @param threadPoolCoreSize grpc thread pool core size
     * @param threadPoolMaxSize  grpc thread pool max size
     * @return rpc client.
     */
    public static RpcClient createClusterClient(String clientName, ConnectionType connectionType,
                                                Integer threadPoolCoreSize, Integer threadPoolMaxSize, Map<String, String> labels) {
        return createClusterClient(clientName, connectionType, threadPoolCoreSize, threadPoolMaxSize, labels, null);
    }

    /**
     * createClusterClient.
     * @param clientName client name.
     * @param connectionType connectionType.
     * @param threadPoolCoreSize coreSize.
     * @param threadPoolMaxSize threadPoolSize.
     * @param labels  tables.
     * @param tlsConfig tlsConfig.
     * @return
     */

    public static RpcClient createClusterClient(String clientName, ConnectionType connectionType, Integer threadPoolCoreSize,
                                                Integer threadPoolMaxSize, Map<String, String> labels, RpcClientTlsConfig tlsConfig) {
        if (!ConnectionType.GRPC.equals(connectionType)) {
            throw new UnsupportedOperationException("unsupported connection type :" + connectionType.getType());
        }

        return CLIENT_MAP.computeIfAbsent(clientName,
                clientNameInner -> new GrpcClusterClient(clientNameInner, threadPoolCoreSize, threadPoolMaxSize,
                        labels, tlsConfig));
    }
}
