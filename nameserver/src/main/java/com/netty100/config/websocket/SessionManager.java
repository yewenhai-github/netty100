package com.netty100.config.websocket;


import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author why
 */
public class SessionManager {

    private static final Map<Integer, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(Integer userId, Session session) {
        SESSIONS.put(userId, session);
    }

    public static void delete(Integer userId) {
         SESSIONS.remove(userId);
    }

    public static int count() {
        return SESSIONS.size();
    }

    public static Session get(Integer userId) {
        return SESSIONS.get(userId);
    }

    public static Map<Integer, Session> list() {
        return SESSIONS;
    }
}
