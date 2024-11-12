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

package com.netty100.cluster.common.http;

import com.netty100.cluster.common.http.client.CapAsyncRestTemplate;
import com.netty100.cluster.common.http.client.CapRestTemplate;
import com.netty100.cluster.common.utils.ExceptionUtil;
import com.netty100.cluster.common.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create a rest template to ensure that each custom client config and rest template are in one-to-one correspondence.
 *
 * @author yewenhai
 */
public final class HttpClientBeanHolder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientBeanHolder.class);
    
    private static final Map<String, CapRestTemplate> SINGLETON_REST = new HashMap<>(10);
    
    private static final Map<String, CapAsyncRestTemplate> SINGLETON_ASYNC_REST = new HashMap<>(10);
    
    private static final AtomicBoolean ALREADY_SHUTDOWN = new AtomicBoolean(false);
    
    static {
        ThreadUtils.addShutdownHook(HttpClientBeanHolder::shutdown);
    }
    
    public static CapRestTemplate getNacosRestTemplate(Logger logger) {
        return getNacosRestTemplate(new DefaultHttpClientFactory(logger));
    }
    
    public static CapRestTemplate getNacosRestTemplate(HttpClientFactory httpClientFactory) {
        if (httpClientFactory == null) {
            throw new NullPointerException("httpClientFactory is null");
        }
        String factoryName = httpClientFactory.getClass().getName();
        CapRestTemplate capRestTemplate = SINGLETON_REST.get(factoryName);
        if (capRestTemplate == null) {
            synchronized (SINGLETON_REST) {
                capRestTemplate = SINGLETON_REST.get(factoryName);
                if (capRestTemplate != null) {
                    return capRestTemplate;
                }
                capRestTemplate = httpClientFactory.createNacosRestTemplate();
                SINGLETON_REST.put(factoryName, capRestTemplate);
            }
        }
        return capRestTemplate;
    }
    
    public static CapAsyncRestTemplate getNacosAsyncRestTemplate(Logger logger) {
        return getNacosAsyncRestTemplate(new DefaultHttpClientFactory(logger));
    }
    
    public static CapAsyncRestTemplate getNacosAsyncRestTemplate(HttpClientFactory httpClientFactory) {
        if (httpClientFactory == null) {
            throw new NullPointerException("httpClientFactory is null");
        }
        String factoryName = httpClientFactory.getClass().getName();
        CapAsyncRestTemplate capAsyncRestTemplate = SINGLETON_ASYNC_REST.get(factoryName);
        if (capAsyncRestTemplate == null) {
            synchronized (SINGLETON_ASYNC_REST) {
                capAsyncRestTemplate = SINGLETON_ASYNC_REST.get(factoryName);
                if (capAsyncRestTemplate != null) {
                    return capAsyncRestTemplate;
                }
                capAsyncRestTemplate = httpClientFactory.createNacosAsyncRestTemplate();
                SINGLETON_ASYNC_REST.put(factoryName, capAsyncRestTemplate);
            }
        }
        return capAsyncRestTemplate;
    }
    
    /**
     * Shutdown common http client.
     */
    private static void shutdown() {
        if (!ALREADY_SHUTDOWN.compareAndSet(false, true)) {
            return;
        }
        LOGGER.warn("[HttpClientBeanHolder] Start destroying common HttpClient");
        
        try {
            shutdown(DefaultHttpClientFactory.class.getName());
        } catch (Exception ex) {
            LOGGER.error("An exception occurred when the common HTTP client was closed : {}",
                    ExceptionUtil.getStackTrace(ex));
        }
        
        LOGGER.warn("[HttpClientBeanHolder] Destruction of the end");
    }
    
    /**
     * Shutdown http client holder and close remove template.
     *
     * @param className HttpClientFactory implement class name
     * @throws Exception ex
     */
    public static void shutdown(String className) throws Exception {
        shutdownNacostSyncRest(className);
        shutdownNacosAsyncRest(className);
    }
    
    /**
     * Shutdown sync http client holder and remove template.
     *
     * @param className HttpClientFactory implement class name
     * @throws Exception ex
     */
    public static void shutdownNacostSyncRest(String className) throws Exception {
        final CapRestTemplate capRestTemplate = SINGLETON_REST.get(className);
        if (capRestTemplate != null) {
            capRestTemplate.close();
            SINGLETON_REST.remove(className);
        }
    }
    
    /**
     * Shutdown async http client holder and remove template.
     *
     * @param className HttpClientFactory implement class name
     * @throws Exception ex
     */
    public static void shutdownNacosAsyncRest(String className) throws Exception {
        final CapAsyncRestTemplate capAsyncRestTemplate = SINGLETON_ASYNC_REST.get(className);
        if (capAsyncRestTemplate != null) {
            capAsyncRestTemplate.close();
            SINGLETON_ASYNC_REST.remove(className);
        }
    }
}
