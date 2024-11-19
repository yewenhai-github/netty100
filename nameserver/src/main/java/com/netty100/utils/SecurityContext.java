package com.netty100.utils;

import com.netty100.entity.User;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author why
 */
public class SecurityContext {

    private final static ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected synchronized Map<String, Object> initialValue() {
            return new HashMap<>(4);
        }
    };

    private static final String USER_KEY = "nameserver-user";

    public static void setUser(@NonNull User user) {
        CONTEXT.get().put(USER_KEY, user);
    }

    public static User getUser() {
        return (User) CONTEXT.get().get(USER_KEY);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
