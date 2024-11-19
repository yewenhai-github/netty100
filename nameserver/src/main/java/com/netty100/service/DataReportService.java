package com.netty100.service;

import com.netty100.entity.Server;
import com.netty100.pojo.dto.*;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface DataReportService {

    void clientConnect(ClientConnectDto dto);

    void clientDisconnect(ClientDisconnectDto dto);

    void clientErrorDisconnect(ClientDisconnectDto dto);

    void clientHeartBeatDisconnect(ClientDisconnectDto dto);

    void serverConnect(ServerConnectDto dto);

    void serverDisconnect(ServerDisconnectDto dto);

    void serverErrorDisconnect(ServerDisconnectDto dto);

    void connectionHistoryDel();

    void serverHeartbeatDisconnect(ServerDisconnectDto dto);

    void shutdown(ServerShutdownDto dto);

    void deleteConnections(Server server);
}
