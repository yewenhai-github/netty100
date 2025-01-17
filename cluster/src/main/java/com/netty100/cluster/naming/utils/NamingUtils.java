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
import com.netty100.cluster.api.utils.StringUtils;

import java.util.regex.Pattern;

import static com.netty100.cluster.api.common.Constants.CLUSTER_NAME_PATTERN_STRING;
import static com.netty100.cluster.api.common.Constants.NUMBER_PATTERN_STRING;

/**
 * NamingUtils.
 *
 * @author why
 * @since 1.0.0
 */
public class NamingUtils {
    
    private static final Pattern CLUSTER_NAME_PATTERN = Pattern.compile(CLUSTER_NAME_PATTERN_STRING);
    
    private static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_PATTERN_STRING);
    
    /**
     * Returns a combined string with serviceName and groupName. serviceName can not be nil.
     *
     * <p>In most cases, serviceName can not be nil. In other cases, for search or anything, See {@link
     * NamingUtils#getGroupedNameOptional(String, String)}
     *
     * <p>etc:
     * <p>serviceName | groupName | result</p>
     * <p>serviceA    | groupA    | groupA@@serviceA</p>
     * <p>nil         | groupA    | threw IllegalArgumentException</p>
     *
     * @return 'groupName@@serviceName'
     */
    public static String getGroupedName(final String serviceName, final String groupName) {
        if (StringUtils.isBlank(serviceName)) {
            throw new IllegalArgumentException("Param 'serviceName' is illegal, serviceName is blank");
        }
        if (StringUtils.isBlank(groupName)) {
            throw new IllegalArgumentException("Param 'groupName' is illegal, groupName is blank");
        }
        final String resultGroupedName = groupName + Constants.SERVICE_INFO_SPLITER + serviceName;
        return resultGroupedName.intern();
    }
    
    public static String getServiceName(final String serviceNameWithGroup) {
        if (StringUtils.isBlank(serviceNameWithGroup)) {
            return StringUtils.EMPTY;
        }
        if (!serviceNameWithGroup.contains(Constants.SERVICE_INFO_SPLITER)) {
            return serviceNameWithGroup;
        }
        return serviceNameWithGroup.split(Constants.SERVICE_INFO_SPLITER)[1];
    }
    
    public static String getGroupName(final String serviceNameWithGroup) {
        if (StringUtils.isBlank(serviceNameWithGroup)) {
            return StringUtils.EMPTY;
        }
        if (!serviceNameWithGroup.contains(Constants.SERVICE_INFO_SPLITER)) {
            return Constants.DEFAULT_GROUP;
        }
        return serviceNameWithGroup.split(Constants.SERVICE_INFO_SPLITER)[0];
    }
    
    /**
     * check combineServiceName format. the serviceName can't be blank.
     * <pre>
     * serviceName = "@@";                 the length = 0; illegal
     * serviceName = "group@@";            the length = 1; illegal
     * serviceName = "@@serviceName";      the length = 2; illegal
     * serviceName = "group@@serviceName"; the length = 2; legal
     * </pre>
     *
     * @param combineServiceName such as: groupName@@serviceName
     */
    public static void checkServiceNameFormat(String combineServiceName) {
        String[] split = combineServiceName.split(Constants.SERVICE_INFO_SPLITER);
        if (split.length <= 1) {
            throw new IllegalArgumentException(
                    "Param 'serviceName' is illegal, it should be format as 'groupName@@serviceName'");
        }
        if (split[0].isEmpty()) {
            throw new IllegalArgumentException("Param 'serviceName' is illegal, groupName can't be empty");
        }
    }
    
    /**
     * Returns a combined string with serviceName and groupName. Such as 'groupName@@serviceName'
     * <p>This method works similar with {@link NamingUtils#getGroupedName} But not
     * verify any parameters.
     *
     * </p> etc:
     * <p>serviceName | groupName | result</p>
     * <p>serviceA    | groupA    | groupA@@serviceA</p>
     * <p>nil         | groupA    | groupA@@</p>
     * <p>nil         | nil       | @@</p>
     *
     * @return 'groupName@@serviceName'
     */
    public static String getGroupedNameOptional(final String serviceName, final String groupName) {
        return groupName + Constants.SERVICE_INFO_SPLITER + serviceName;
    }
    
    /**
     * Check string is a number or not.
     *
     * @param str a string of digits
     * @return if it is a string of digits, return true
     */
    public static boolean isNumber(String str) {
        return !StringUtils.isEmpty(str) && NUMBER_PATTERN.matcher(str).matches();
    }
}
