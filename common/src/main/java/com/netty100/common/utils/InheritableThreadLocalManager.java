package com.netty100.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author why
 * @version 1.0.0, 2022/6/10
 * @since 1.0.0, 2022/6/10
 */
public class InheritableThreadLocalManager {

    private final static InheritableThreadLocal<Map<String, Object>> cacheThreadLocal = new InheritableThreadLocal<Map<String, Object>>() {
        @Override
        protected synchronized Map<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    public static void setAttribute(String var, Object value) {
        cacheThreadLocal.get().put(var, value);
    }

    public static Object getAttribute(String key) {
        return cacheThreadLocal.get().get(key);
    }

    public static void destoryValue() {
        cacheThreadLocal.get().clear();
    }

}
