package com.netty100.service;

import com.netty100.entity.ClientHeartbeatLog;

import java.util.List;

/**
 * @author why
 */
public interface ClientHeartbeatLogService {

    void processData(List<ClientHeartbeatLog> data);
}
