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

package com.netty100.cluster.naming.utils;

import com.netty100.cluster.api.common.Constants;
import com.netty100.cluster.api.naming.pojo.ServiceInfo;
import com.netty100.cluster.common.utils.StringUtils;
import com.netty100.cluster.api.naming.pojo.Instance;
import com.netty100.cluster.naming.core.v2.metadata.ServiceMetadata;
import com.netty100.cluster.naming.misc.Loggers;
import com.netty100.cluster.naming.pojo.Subscriber;
//import com.netty100.cluster.naming.selector.SelectorManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service util.
 *
 * @author why
 */
public final class ServiceUtil {
    
    /**
     * Page service name.
     *
     * @param pageNo         page number
     * @param pageSize       size per page
     * @param serviceNameSet service name set
     * @return service name list by paged
     */
    public static List<String> pageServiceName(int pageNo, int pageSize, Collection<String> serviceNameSet) {
        List<String> result = new ArrayList<>(serviceNameSet);
        int start = (pageNo - 1) * pageSize;
        if (start < 0) {
            start = 0;
        }
        if (start >= result.size()) {
            return Collections.emptyList();
        }
        int end = start + pageSize;
        if (end > result.size()) {
            end = result.size();
        }
        for (int i = start; i < end; i++) {
            String serviceName = result.get(i);
            int indexOfSplitter = serviceName.indexOf(Constants.SERVICE_INFO_SPLITER);
            if (indexOfSplitter > 0) {
                serviceName = serviceName.substring(indexOfSplitter + 2);
            }
            result.set(i, serviceName);
        }
        return result.subList(start, end);
    }
    
    /**
     * Select healthy instance of service info.
     *
     * @param serviceInfo original service info
     * @return new service info
     */
    public static ServiceInfo selectHealthyInstances(ServiceInfo serviceInfo) {
        return selectInstances(serviceInfo, true, false);
    }
    
    /**
     * Select healthy instance of service info.
     *
     * @param serviceInfo original service info
     * @return new service info
     */
    public static ServiceInfo selectEnabledInstances(ServiceInfo serviceInfo) {
        return selectInstances(serviceInfo, false, true);
    }
    
    /**
     * Select instance of service info.
     *
     * @param serviceInfo original service info
     * @param cluster     cluster of instances
     * @return new service info
     */
    public static ServiceInfo selectInstances(ServiceInfo serviceInfo, String cluster) {
        return selectInstances(serviceInfo, cluster, false, false);
    }
    
    /**
     * Select instance of service info.
     *
     * @param serviceInfo original service info
     * @param healthyOnly whether only select instance which healthy
     * @param enableOnly  whether only select instance which enabled
     * @return new service info
     */
    public static ServiceInfo selectInstances(ServiceInfo serviceInfo, boolean healthyOnly, boolean enableOnly) {
        return selectInstances(serviceInfo, StringUtils.EMPTY, healthyOnly, enableOnly);
    }
    
    /**
     * Select instance of service info.
     *
     * @param serviceInfo original service info
     * @param cluster     cluster of instances
     * @param healthyOnly whether only select instance which healthy
     * @return new service info
     */
    public static ServiceInfo selectInstances(ServiceInfo serviceInfo, String cluster, boolean healthyOnly) {
        return selectInstances(serviceInfo, cluster, healthyOnly, false);
    }
    
    /**
     * Select instance of service info.
     *
     * @param serviceInfo original service info
     * @param cluster     cluster of instances
     * @param healthyOnly whether only select instance which healthy
     * @param enableOnly  whether only select instance which enabled
     * @return new service info
     */
    public static ServiceInfo selectInstances(ServiceInfo serviceInfo, String cluster, boolean healthyOnly,
            boolean enableOnly) {
        return doSelectInstances(serviceInfo, cluster, healthyOnly, enableOnly, null);
    }
    
    /**
     * Select instance of service info with healthy protection.
     *
     * @param serviceInfo     original service info
     * @param serviceMetadata service meta info
     * @param subscriber subscriber
     * @return new service info
     */
    public static ServiceInfo selectInstancesWithHealthyProtection(ServiceInfo serviceInfo, ServiceMetadata serviceMetadata, Subscriber subscriber) {
        return selectInstancesWithHealthyProtection(serviceInfo, serviceMetadata, subscriber.getCluster(), false, false, subscriber.getIp());
    }

    /**
     * Select instance of service info with healthy protection.
     *
     * @param serviceInfo     original service info
     * @param serviceMetadata service meta info
     * @param healthyOnly     whether only select instance which healthy
     * @param enableOnly      whether only select instance which enabled
     * @param subscriber subscriber
     * @return new service info
     */
    public static ServiceInfo selectInstancesWithHealthyProtection(ServiceInfo serviceInfo,
            ServiceMetadata serviceMetadata, boolean healthyOnly, boolean enableOnly, Subscriber subscriber) {
        return selectInstancesWithHealthyProtection(serviceInfo, serviceMetadata, subscriber.getCluster(), healthyOnly,
                enableOnly, subscriber.getIp());
    }

    /**
     * Select instance of service info with healthy protection.
     *
     * @param serviceInfo     original service info
     * @param serviceMetadata service meta info
     * @param cluster         cluster of instances
     * @param healthyOnly     whether only select instance which healthy
     * @param enableOnly      whether only select instance which enabled
     * @param subscriberIp subscriber ip address
     * @return new service info
     */
    public static ServiceInfo selectInstancesWithHealthyProtection(ServiceInfo serviceInfo, ServiceMetadata serviceMetadata, String cluster,
            boolean healthyOnly, boolean enableOnly, String subscriberIp) {
        InstancesFilter filter = (filteredResult, allInstances, healthyCount) -> {
            if (serviceMetadata == null) {
                return;
            }
            allInstances = filteredResult.getHosts();
            int originalTotal = allInstances.size();
            // filter ips using selector
//            SelectorManager selectorManager = ApplicationUtils.getBean(SelectorManager.class);
//            allInstances = selectorManager.select(serviceMetadata.getSelector(), subscriberIp, allInstances);
            filteredResult.setHosts(allInstances);
            
            // will re-compute healthCount
            long newHealthyCount = healthyCount;
            if (originalTotal != allInstances.size()) {
                for (Instance allInstance : allInstances) {
                    if (allInstance.isHealthy()) {
                        newHealthyCount++;
                    }
                }
            }
            
            float threshold = serviceMetadata.getProtectThreshold();
            if (threshold < 0) {
                threshold = 0F;
            }
            if ((float) newHealthyCount / allInstances.size() <= threshold) {
                Loggers.SRV_LOG.warn("protect threshold reached, return all ips, service: {}", filteredResult.getName());
                filteredResult.setReachProtectionThreshold(true);
                List<Instance> filteredInstances = allInstances.stream()
                        .map(i -> {
                            if (!i.isHealthy()) {
                                i = InstanceUtil.deepCopy(i);
                                // set all to `healthy` state to protect
                                i.setHealthy(true);
                            } // else deepcopy is unnecessary
                            return i;
                        })
                        .collect(Collectors.toCollection(LinkedList::new));
                filteredResult.setHosts(filteredInstances);
            }
        };
        return doSelectInstances(serviceInfo, cluster, healthyOnly, enableOnly, filter);
    }

    /**
     * Select instance of service info.
     *
     * @param serviceInfo original service info
     * @param cluster     cluster of instances
     * @param healthyOnly whether only select instance which healthy
     * @param enableOnly  whether only select instance which enabled
     * @param filter      do some other filter operation
     * @return new service info
     */
    private static ServiceInfo doSelectInstances(ServiceInfo serviceInfo, String cluster,
                                                 boolean healthyOnly, boolean enableOnly,
                                                 InstancesFilter filter) {
        ServiceInfo result = new ServiceInfo();
        result.setName(serviceInfo.getName());
        result.setGroupName(serviceInfo.getGroupName());
        result.setCacheMillis(serviceInfo.getCacheMillis());
        result.setLastRefTime(System.currentTimeMillis());
        result.setClusters(cluster);
        result.setReachProtectionThreshold(false);
        Set<String> clusterSets = StringUtils.isNotBlank(cluster) ? new HashSet<>(
                Arrays.asList(cluster.split(","))) : new HashSet<>();
        long healthyCount = 0L;
        // The instance list won't be modified almost time.
        List<Instance> filteredInstances = new LinkedList<>();
        // The instance list of all filtered by cluster/enabled condition.
        List<Instance> allInstances = new LinkedList<>();
        for (Instance ip : serviceInfo.getHosts()) {
            if (checkCluster(clusterSets, ip) && checkEnabled(enableOnly, ip)) {
                if (!healthyOnly || ip.isHealthy()) {
                    filteredInstances.add(ip);
                }
                if (ip.isHealthy()) {
                    healthyCount += 1;
                }
                allInstances.add(ip);
            }
        }
        result.setHosts(filteredInstances);
        if (filter != null) {
            filter.doFilter(result, allInstances, healthyCount);
        }
        return result;
    }
    
    private static boolean checkCluster(Set<String> clusterSets, Instance instance) {
        if (clusterSets.isEmpty()) {
            return true;
        }
        return clusterSets.contains(instance.getClusterName());
    }
    
    private static boolean checkEnabled(boolean enableOnly, Instance instance) {
        return !enableOnly || instance.isEnabled();
    }

    private interface InstancesFilter {

        /**
         * Do customized filtering.
         *
         * @param filteredResult result with instances already been filtered cluster/enabled/healthy
         * @param allInstances   all instances filtered by cluster/enabled
         * @param healthyCount   healthy instances count filtered by cluster/enabled
         */
        void doFilter(ServiceInfo filteredResult,
                      List<Instance> allInstances,
                      long healthyCount);

    }

}
