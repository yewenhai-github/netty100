package com.netty100.service;

import com.netty100.entity.ServerHeartBeatLog;

import java.util.List;

/**
 * @author why
 */
public interface ServerHeartbeatLogService {

    void processData(List<ServerHeartBeatLog> data);
}
