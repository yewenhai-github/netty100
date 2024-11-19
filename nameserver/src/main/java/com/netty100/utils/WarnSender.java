package com.netty100.utils;

import java.util.List;

/**
 * @author why
 */
public interface WarnSender {

    void sendWarn(List<Integer> userIds, String title, String content);
}
