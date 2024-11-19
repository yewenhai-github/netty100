package com.netty100.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author why
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class MessageSender {

    private ThreadPoolTaskExecutor executor;

    private ObjectMapper objectMapper;

    private final Lock lock = new ReentrantLock(false);

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }


    public boolean sendTextMessage(@NonNull Integer userId, @NonNull Object message) {
        Session session = SessionManager.get(userId);
        if (Objects.isNull(session)) {
            log.warn("userId = {}的用户不在线", userId);
            return false;
        } else if (!session.isOpen()) {
            log.warn("session未打开,消息发送失败,userId = {}", userId);
            return false;
        } else {
            return sendTextMessage(session, message, userId);
        }
    }

    public void broadcastTextMessage(@NonNull String message) {
        executor.execute(() -> SessionManager.list().forEach((k, v) -> sendTextMessage(v, message, k)));
    }

    public boolean sendTextMessage(Session session, Object message, Integer userId) {
        try {
            lock.lock();
            session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
            return true;
        } catch (IOException e) {
            log.error("消息发送失败,消息接收者:{},消息内容:{}", userId, message, e);
            return false;
        } finally {
            lock.unlock();
        }
    }
}
