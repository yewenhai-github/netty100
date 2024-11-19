package com.netty100.service;

import com.netty100.entity.ClientConnectionHistory;

import java.util.List;

/**
 * @author why
 */
public interface ClientConnectionHistoryService {

    void batchSave(String tableName, List<ClientConnectionHistory> list);

    ClientConnectionHistory getLatestDisconnectRecord(String tableName, String channelId);

    List<Integer> selectClearIds(String tableName, String delEndTime, Integer defaultReadPageSize);

    void deleteConnectionLogs(String tableName, List<Integer> clearIds);
}
