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

package com.netty100.cluster.core.cluster.lookup;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.netty100.cluster.api.exception.CapException;
import com.netty100.cluster.common.http.HttpClientBeanHolder;
import com.netty100.cluster.common.http.client.CapRestTemplate;
import com.netty100.cluster.common.utils.ExceptionUtil;
import com.netty100.cluster.core.cluster.AbstractMemberLookup;
import com.netty100.cluster.core.cluster.MemberUtil;
import com.netty100.cluster.core.route.TopeCloudRoute;
import com.netty100.cluster.core.utils.GenericType;
import com.netty100.cluster.core.utils.GlobalExecutor;
import com.netty100.cluster.core.utils.Loggers;
import com.netty100.cluster.sys.env.EnvUtil;
import com.netty100.common.http.RemotingHttpResult;
import com.netty100.common.http.RemotingHttpUtil;
import com.netty100.common.protocol.RequestCode;
import com.netty100.common.utils.SysUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * Cluster member addressing mode for the address server.
 *
 * @author why
 */
public class AddressServerMemberLookup extends AbstractMemberLookup {
    
    private final GenericType<String> genericType = new GenericType<String>() { };
    
    public String domainName;
    
    public String domainPort;
    
    public String addressServerUrl;
    public String addressStartServerUrl;
    public String addressHeatBeatServerUrl;
    public String domainToken;

    private volatile boolean isAddressServerHealth = true;
    
    private int addressServerFailCount = 0;
    
    private int maxFailCount = 12;
    
    private final CapRestTemplate restTemplate = HttpClientBeanHolder.getNacosRestTemplate(Loggers.CORE);
    
    private volatile boolean shutdown = false;
    
    public static final String HEALTH_CHECK_FAIL_COUNT_PROPERTY = "maxHealthCheckFailCount";
    
    public static final String DEFAULT_HEALTH_CHECK_FAIL_COUNT = "12";
    
    public static final String DEFAULT_SERVER_DOMAIN = "127.0.0.1";
    
    public static final String DEFAULT_SERVER_POINT = "80";
    
    public static final int DEFAULT_SERVER_RETRY_TIME = 3;
    
    public static final long DEFAULT_SYNC_TASK_DELAY_MS = 5_000L;
    
    public static final String ADDRESS_SERVER_DOMAIN_ENV = "address_server_domain";
    
    public static final String ADDRESS_SERVER_DOMAIN_PROPERTY = "address.server.domain";
    
    public static final String ADDRESS_SERVER_PORT_ENV = "address_server_port";
    
    public static final String ADDRESS_SERVER_PORT_PROPERTY = "address.server.port";
    
    public static final String ADDRESS_SERVER_URL_ENV = "address_server_url";
    
    public static final String ADDRESS_SERVER_URL_PROPERTY = "address.server.url";
    public static final String NAME_SERVER_DOMAIN_PROPERTY = "com.netty100.cloud.domain";
    public static final String NAME_SERVER_TOKEN_PROPERTY = "com.netty100.cloud.token";

    public static final String ADDRESS_SERVER_RETRY_PROPERTY = "cap.core.address-server.retry";
    
    @Override
    public void doStart() throws CapException {
        this.maxFailCount = Integer.parseInt(EnvUtil.getProperty(HEALTH_CHECK_FAIL_COUNT_PROPERTY, DEFAULT_HEALTH_CHECK_FAIL_COUNT));
        initKernelSys();
        initAddressStart();
        run();
    }
    
    @Override
    public boolean useKernelServer() {
        return true;
    }
    
    private void initKernelSys() {
        //application.properties 中的配置
        domainName = EnvUtil.getProperty(NAME_SERVER_DOMAIN_PROPERTY, DEFAULT_SERVER_DOMAIN);
        domainPort = EnvUtil.getProperty(ADDRESS_SERVER_PORT_PROPERTY, DEFAULT_SERVER_POINT);
        domainToken = EnvUtil.getProperty(NAME_SERVER_TOKEN_PROPERTY);

        //环境变量中的设置
        domainName = SysUtility.isNotEmpty(System.getenv(ADDRESS_SERVER_DOMAIN_ENV)) ? System.getenv(ADDRESS_SERVER_DOMAIN_ENV) : domainName;
        domainPort = SysUtility.isNotEmpty(System.getenv(ADDRESS_SERVER_PORT_ENV)) ? System.getenv(ADDRESS_SERVER_PORT_ENV): domainPort;

        //获取
        addressServerUrl = domainName + ":" + domainPort + TopeCloudRoute.uriMap.get(RequestCode.Req03.getCode());
        //启动注册
        addressStartServerUrl = domainName + ":" + domainPort + TopeCloudRoute.uriMap.get(RequestCode.Req04.getCode());
        //定时上报
        addressHeatBeatServerUrl = domainName + ":" + domainPort + TopeCloudRoute.uriMap.get(RequestCode.Req05.getCode());

        Loggers.CORE.info("ServerListService address-server port:" + domainPort);
        Loggers.CORE.info("ADDRESS_SERVER_URL:" + addressServerUrl);
    }

    private void initAddressStart() {
        if(addressStartServerUrl.indexOf(DEFAULT_SERVER_DOMAIN) >= 0){
            return;
        }

        JSONObject params = new JSONObject();
        params.put("intranetIp", SysUtility.getHostIp());//内网地址
        params.put("port", EnvUtil.getPort());//绑定端口号
        params.put("cluster", EnvUtil.getCluster());
        params.put("lastBootTime", SysUtility.getSysDate());//启动时间,时间格式为yyyy-MM-dd HH:mm:ss
        RemotingHttpUtil.postBody(addressStartServerUrl, domainToken, 3, params, 0);
    }

    @SuppressWarnings("PMD.UndefineMagicConstantRule")
    private void run() throws CapException {
        // With the address server, you need to perform a synchronous member node pull at startup
        // Repeat three times, successfully jump out
        boolean success = false;
        Throwable ex = null;
        int maxRetry = EnvUtil.getProperty(ADDRESS_SERVER_RETRY_PROPERTY, Integer.class, DEFAULT_SERVER_RETRY_TIME);
        for (int i = 0; i < maxRetry; i++) {
            try {
                syncFromAddressUrl();
                success = true;
                break;
            } catch (Throwable e) {
                ex = e;
                Loggers.CLUSTER.error("[serverlist] exception, error : {}", ExceptionUtil.getAllExceptionMsg(ex));
            }
        }
        if (!success) {
            throw new CapException(CapException.SERVER_ERROR, ex);
        }
        
        GlobalExecutor.scheduleByCommon(new AddressServerSyncTask(), DEFAULT_SYNC_TASK_DELAY_MS);
    }
    
    @Override
    protected void doDestroy() throws CapException {
        shutdown = true;
    }
    
    @Override
    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<>(4);
        info.put("addressServerHealth", isAddressServerHealth);
        info.put("addressServerUrl", addressServerUrl);
        info.put("addressServerFailCount", addressServerFailCount);
        return info;
    }
    
    private void syncFromAddressUrl() throws Exception {
        if(addressServerUrl.indexOf(DEFAULT_SERVER_DOMAIN) >= 0){
            return;
        }

        JSONObject params = new JSONObject();
        params.put("intranetIp", SysUtility.getHostIp());
        params.put("port", EnvUtil.getPort());

        RemotingHttpResult result = RemotingHttpUtil.postBody(addressServerUrl, domainToken, 3, params, 0);
        if(200 == result.getResponseCode()){
            isAddressServerHealth = true;
            try {
                afterLookup(MemberUtil.readServerConf(EnvUtil.analyzeAddressServerClusterConf(result)));
            } catch (Throwable e) {
                Loggers.CLUSTER.error("[serverlist] exception for analyzeClusterConf, error : {}",
                        ExceptionUtil.getAllExceptionMsg(e));
            }
            addressServerFailCount = 0;
        } else {
            addressServerFailCount++;
            if (addressServerFailCount >= maxFailCount) {
                isAddressServerHealth = false;
            }
            Loggers.CLUSTER.error("[serverlist] failed to get serverlist, error code {}", result.getResponseCode());
        }
    }

    private void syncToAddressUrl() throws Exception {
        if(addressHeatBeatServerUrl.indexOf(DEFAULT_SERVER_DOMAIN) >= 0){
            return;
        }
        // 加载类
        Class<?> clazz = Class.forName("com.netty100.kernel.utils.TopeChannelUtils");
        // 创建对象（如果方法是非静态的）
        Object obj = ReflectUtil.newInstance(clazz);

        JSONObject request = new JSONObject();
        request.put("intranetIp", SysUtility.getHostIp());//内网地址
        request.put("port", EnvUtil.getPort());//绑定端口号
        request.put("clientConnectCount", ReflectUtil.invoke(obj, ReflectUtil.getMethod(clazz, "getClientUserCount")));
        request.put("serverConnectCount", ReflectUtil.invoke(obj, ReflectUtil.getMethod(clazz, "getServerUserCount")));
        RemotingHttpUtil.postBody(addressHeatBeatServerUrl, domainToken, 3, request, 0);
    }

    class AddressServerSyncTask implements Runnable {
        @Override
        public void run() {
            if (shutdown) {
                return;
            }
            try {
                syncFromAddressUrl();
                syncToAddressUrl();
            } catch (Throwable ex) {
                addressServerFailCount++;
                if (addressServerFailCount >= maxFailCount) {
                    isAddressServerHealth = false;
                }
                Loggers.CLUSTER.error("[serverlist] exception, error : {}", ExceptionUtil.getAllExceptionMsg(ex));
            } finally {
                GlobalExecutor.scheduleByCommon(this, DEFAULT_SYNC_TASK_DELAY_MS);
            }
        }
    }
}
