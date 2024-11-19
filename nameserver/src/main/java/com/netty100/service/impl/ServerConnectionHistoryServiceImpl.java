package com.netty100.service.impl;

import com.netty100.entity.ServerConnectionHistory;
import com.netty100.mapper.ServerConnectionHistoryMapper;
import com.netty100.service.ServerConnectionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author why
 */
@Service
public class ServerConnectionHistoryServiceImpl implements ServerConnectionHistoryService {

    private ServerConnectionHistoryMapper serverConnectionHistoryMapper;

    @Autowired
    public void setServerConnectionHistoryMapper(ServerConnectionHistoryMapper serverConnectionHistoryMapper) {
        this.serverConnectionHistoryMapper = serverConnectionHistoryMapper;
    }

    @Override
    public void batchSave(List<ServerConnectionHistory> x) {
        serverConnectionHistoryMapper.batchSave(x);
    }

    @Override
    public ServerConnectionHistory getLatestDisconnectRecord(String channelId) {
        return serverConnectionHistoryMapper.getLatestDisconnectRecord(channelId);
    }
}
