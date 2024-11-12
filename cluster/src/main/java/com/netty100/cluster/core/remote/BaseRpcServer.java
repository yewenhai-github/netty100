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

package com.netty100.cluster.core.remote;

import com.netty100.cluster.common.remote.ConnectionType;
import com.netty100.cluster.common.remote.PayloadRegistry;
import com.netty100.cluster.common.utils.JacksonUtils;
import com.netty100.cluster.core.utils.Loggers;
import com.netty100.cluster.sys.env.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * abstract rpc server .
 *
 * @author yewenhai
 * @version $Id: BaseRpcServer.java, v 0.1 2020年07月13日 3:41 PM liuzunfei Exp $
 */
public abstract class BaseRpcServer {

    static {
        PayloadRegistry.init();
    }

    @Autowired
    protected RpcServerTlsConfig grpcServerConfig;

    /**
     * Start sever.
     */
    @PostConstruct
    public void start() throws Exception {
        String serverName = getClass().getSimpleName();
        String tlsConfig = JacksonUtils.toJson(grpcServerConfig);
        Loggers.REMOTE.info("Cap {} Rpc server starting at port {} and tls config:{}", serverName, getServicePort(), tlsConfig);
        
        startServer();
    
        Loggers.REMOTE.info("Cap {} Rpc server started at port {} and tls config:{}", serverName, getServicePort(), tlsConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Loggers.REMOTE.info("Cap {} Rpc server stopping", serverName);
            try {
                BaseRpcServer.this.stopServer();
                Loggers.REMOTE.info("Cap {} Rpc server stopped successfully...", serverName);
            } catch (Exception e) {
                Loggers.REMOTE.error("Cap {} Rpc server stopped fail...", serverName, e);
            }
        }));

    }
    
    /**
     * get connection type.
     *
     * @return connection type.
     */
    public abstract ConnectionType getConnectionType();
    
    /**
     * Start sever.
     *
     * @throws Exception exception throw if start server fail.
     */
    public abstract void startServer() throws Exception;
    
    /**
     * the increase offset of cap server port for rpc server port.
     *
     * @return delta port offset of main port.
     */
    public abstract int rpcPortOffset();
    
    /**
     * get service port.
     *
     * @return service port.
     */
    public int getServicePort() {
        return EnvUtil.getPort() + rpcPortOffset();
    }
    
    /**
     * Stop Server.
     *
     * @throws Exception throw if stop server fail.
     */
    public final void stopServer() throws Exception {
        shutdownServer();
    }
    
    /**
     * the increase offset of cap server port for rpc server port.
     */
    @PreDestroy
    public abstract void shutdownServer();

}
