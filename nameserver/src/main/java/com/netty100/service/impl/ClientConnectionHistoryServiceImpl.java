package com.netty100.service.impl;

import com.netty100.entity.ClientConnectionHistory;
import com.netty100.mapper.ClientConnectionHistoryMapper;
import com.netty100.service.ClientConnectionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author why
 */
@Service
public class ClientConnectionHistoryServiceImpl implements ClientConnectionHistoryService {

    private ClientConnectionHistoryMapper clientConnectionHistoryMapper;

    @Autowired
    public void setClientConnectionHistoryMapper(ClientConnectionHistoryMapper clientConnectionHistoryMapper) {
        this.clientConnectionHistoryMapper = clientConnectionHistoryMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(String tableName, List<ClientConnectionHistory> list) {
       clientConnectionHistoryMapper.batchSave(tableName,list);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientConnectionHistory getLatestDisconnectRecord(String tableName, String channelId) {
        return clientConnectionHistoryMapper.getLatestDisconnectRecord(tableName, channelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> selectClearIds(String tableName, String delEndTime, Integer defaultReadPageSize) {
        return clientConnectionHistoryMapper.selectClearIds(tableName, delEndTime, defaultReadPageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConnectionLogs(String tableName, List<Integer> clearIds) {
        clientConnectionHistoryMapper.deleteConnectionLogs(tableName, clearIds);
    }
}
