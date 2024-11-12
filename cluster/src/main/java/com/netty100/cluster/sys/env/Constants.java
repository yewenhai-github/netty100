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

package com.netty100.cluster.sys.env;

/**
 * Cap common constants.
 *
 * @author yewenhai
 * @since 0.2.2
 */
public interface Constants {
    
    String SYS_MODULE = "sys";
    
    /**
     * Spring Profile : "standalone".
     */
    String STANDALONE_SPRING_PROFILE = "standalone";
    
    /**
     * The System property name of  Standalone mode.
     */
    String STANDALONE_MODE_PROPERTY_NAME = "kernel.standalone";
    
    String STANDALONE_MODE_STATE = "standalone_mode";
    
    /**
     * The System property name of  Function mode.
     */
    String FUNCTION_MODE_PROPERTY_NAME = "cap.functionMode";
    
    String FUNCTION_MODE_STATE = "function_mode";
    
    /**
     * The System property name of prefer hostname over ip.
     */
    String PREFER_HOSTNAME_OVER_IP_PROPERTY_NAME = "cap.preferHostnameOverIp";
    
    /**
     * the root context path.
     */
    String ROOT_WEB_CONTEXT_PATH = "/";
    
    String NACOS_VERSION = "version";
    
    String NACOS_SERVER_IP = "cap.server.ip";
    
    String USE_ONLY_SITE_INTERFACES = "cap.inetutils.use-only-site-local-interfaces";
    String PREFERRED_NETWORKS = "cap.inetutils.preferred-networks";
    String IGNORED_INTERFACES = "cap.inetutils.ignored-interfaces";
    String AUTO_REFRESH_TIME = "cap.core.inet.auto-refresh";
    String IP_ADDRESS = "cap.inetutils.ip-address";
    String PREFER_HOSTNAME_OVER_IP = "cap.inetutils.prefer-hostname-over-ip";
    String SYSTEM_PREFER_HOSTNAME_OVER_IP = "cap.preferHostnameOverIp";
    String WEB_CONTEXT_PATH = "server.servlet.context-path";
    String COMMA_DIVISION = ",";
    
    String NACOS_SERVER_HEADER = "Cap-Server";
    
    String REQUEST_PATH_SEPARATOR = "-->";
    
    String AVAILABLE_PROCESSORS_BASIC = "cap.core.sys.basic.processors";
    
    String SUPPORT_UPGRADE_FROM_1X = "cap.core.support.upgrade.from.1x";
}
