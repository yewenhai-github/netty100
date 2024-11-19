package com.netty100.service;

import com.netty100.entity.ServerConnectionHistory;

import java.util.List;

/**
 * @author why
 */
public interface ServerConnectionHistoryService {

    void batchSave(List<ServerConnectionHistory> x);

    ServerConnectionHistory getLatestDisconnectRecord(String channelId);
}
