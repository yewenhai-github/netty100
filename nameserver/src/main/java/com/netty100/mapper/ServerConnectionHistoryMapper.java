package com.netty100.mapper;

import com.netty100.entity.ServerConnectionHistory;

import java.util.List;

/**
 * @author why
 */
public interface ServerConnectionHistoryMapper {

    void batchSave(List<ServerConnectionHistory> x);

    ServerConnectionHistory getLatestDisconnectRecord(String channelId);
}
