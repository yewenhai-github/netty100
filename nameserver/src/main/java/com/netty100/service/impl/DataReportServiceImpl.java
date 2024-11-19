package com.netty100.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.netty100.config.constant.GlobalConstant;
import com.netty100.entity.*;
import com.netty100.enumeration.ActionType;
import com.netty100.pojo.dto.*;
import com.netty100.pojo.vo.ServerVo;
import com.netty100.service.*;
import com.netty100.timer.config.TaskConfig;
import com.netty100.utils.IpTransformer;
import com.netty100.utils.RedisTool;
import com.netty100.utils.exception.CommonException;
import com.netty100.utils.respons.ResponseMsg;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@SuppressWarnings("all")
@Slf4j
@Service
public class DataReportServiceImpl implements DataReportService {

    private final int batchSize = 500;

    @Value("${servers.connection.clearDays}")
    private int clearDays;

    private ServerService serverService;

    private ThreadPoolTaskExecutor executor;

    private ReportMinuteDataService reportMinuteDataService;

    private IpTransformer ipTransformer;

    private ServerConnectionHistoryService serverConnectionHistoryService;

    private ClientConnectionHistoryService clientConnectionHistoryService;

    private ClientConnectionService clientConnectionService;

    private ServerConnectionService serverConnectionService;

    private HistoricalUserLocationService historicalUserLocationService;

    private ActiveUserLocationService activeUserLocationService;

    private RedisTool redisTool;

    /**
     * 游戏端新建连接上报
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clientConnect(ClientConnectDto dto) {
        //异步执行
        executor.execute(() -> {
                    DbSearcher dbSearcher = null;
                    int total = 0;
                    try {
                        //校验节点
                        Server server = checkServer(dto.getIntranetIp(), dto.getPort());
                        //客户端连接历史分组
                        val clientConnectionHistoryMap = new HashMap<String, List<ClientConnectionHistory>>(16);
                        //客户端用户分布
                        val userLocations = new ArrayList<UserLocation>();
                        //活跃客户端
                        val activeClientConnections = new ArrayList<ClientConnection>();
                        dbSearcher = ipTransformer.getDbSearcher();
                        for (ClientConnectDto.Property property : dto.getProperties()) {
                            total += 1;
                            val location = ipTransformer.transform(dbSearcher, property.getRemoteIp());
                            if (Objects.nonNull(location) && location.length == 2) {
                                val userLocation = new UserLocation(null, location[0], location[1], property.getUserId());
                                userLocations.add(userLocation);
                            }

                            //获取userId对应的历史表
                            val tableName = getTableName(property.getUserId());
                            //历史表处理
                            clientConnectionHistoryMap.compute(tableName, (k, v) -> {
                                if (Objects.isNull(v)) {
                                    v = new ArrayList<>();
                                }
                                v.add(new ClientConnectionHistory(null, server.getId(), server.getClusterId(), dto.getIntranetIp(), property.getChannelId(),
                                        property.getChannelKey(), property.getDeviceId(), property.getUserId(),
                                        property.getRemoteIp(), ActionType.CLIENT_CONNECT, property.getConnectTime()));
                                return v;
                            });
                            //获取最后一次断开记录
                            ClientConnectionHistory record = clientConnectionHistoryService.getLatestDisconnectRecord(tableName, property.getChannelId());
                            if (Objects.isNull(record)) {
                                activeClientConnections.add(new ClientConnection(null, server.getId(), server.getClusterId(), dto.getIntranetIp(), property.getChannelId(), property.getChannelKey(), property.getDeviceId(),
                                        property.getUserId(),
                                        property.getMessageWay(), property.getMessageSource(), property.getMessageDest(), property.getMessageType(), property.getMessageSerialize(), property.getRemoteIp(), property.getConnectTime(),
                                        property.getConnectTime()));
                            }
                        }

                        if (!activeClientConnections.isEmpty()) {
                            Lists.partition(activeClientConnections, batchSize).forEach(x -> {
                                clientConnectionService.batchSave(x);
                            });
                        }
                        clientConnectionHistoryMap.forEach((k, v) -> {
                            if (!v.isEmpty()) {
                                Lists.partition(v, batchSize).forEach(x -> {
                                    clientConnectionHistoryService.batchSave(k, x);
                                });
                            }
                        });
                        if (!userLocations.isEmpty()) {
                            Lists.partition(userLocations, batchSize).forEach(x -> {
                                activeUserLocationService.batchSave(x);
                                historicalUserLocationService.batchSave(x);
                            });
                        }
                        log.info("netty节点:{}游戏端新建连接上报处理成功,共处理{}条", server.getIntranetIp(), total);
                    } catch (Exception e) {
                        log.error("netty节点:{}游戏端新建连接上报处理出现异常", dto.getIntranetIp(), e);
                    } finally {
                        ipTransformer.close(dbSearcher);
                    }
                }
        );
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clientDisconnect(ClientDisconnectDto dto) {
        executor.execute(() -> {
            clientDisconnect(dto, ActionType.CLIENT_DISCONNECT);
        });
    }

    /**
     * 游戏端连接异常断开
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clientErrorDisconnect(ClientDisconnectDto dto) {
        executor.execute(() -> {
            clientDisconnect(dto, ActionType.CLIENT_ERROR_DISCONNECT);
        });
    }

    /**
     * 游戏端连接心跳断开
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clientHeartBeatDisconnect(ClientDisconnectDto dto) {
        executor.execute(() -> {
            clientDisconnect(dto, ActionType.CLIENT_HEARTBEAT_DISCONNECT);
        });
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void serverConnect(ServerConnectDto dto) {
        executor.execute(() -> {
            val adder = new LongAdder();
            Server server = checkServer(dto.getIntranetIp(), dto.getPort());
            val serverConnectionHistoryList = new ArrayList<ServerConnectionHistory>();
            val activeServerConnectionList = new ArrayList<ServerConnection>();
            dto.getProperties().forEach(property -> {
                adder.increment();
                val connectionHistory = new ServerConnectionHistory(null, server.getId(), server.getClusterId(), dto.getIntranetIp(), property.getChannelId(), property.getChannelKey(),
                        property.getRemoteIp(), ActionType.SERVER_CONNECT, property.getConnectTime());
                serverConnectionHistoryList.add(connectionHistory);
                ServerConnectionHistory serverConnectionHistory = serverConnectionHistoryService.getLatestDisconnectRecord(property.getChannelId());
                if (Objects.isNull(serverConnectionHistory)) {
                    activeServerConnectionList.add(new ServerConnection(null, server.getId(), server.getClusterId(), dto.getIntranetIp(), property.getChannelId(), property.getChannelKey(),
                            property.getMessageWay(), property.getMessageSource(), property.getMessageDest(), property.getMessageType(), property.getMessageSerialize(),
                            property.getRemoteIp(), property.getConnectTime(), property.getConnectTime()));
                }
            });
            if (!serverConnectionHistoryList.isEmpty()) {
                Lists.partition(serverConnectionHistoryList, batchSize).forEach(x -> {
                    serverConnectionHistoryService.batchSave(x);
                });
            }
            if (!activeServerConnectionList.isEmpty()) {
                Lists.partition(activeServerConnectionList, batchSize).forEach(x -> {
                    serverConnectionService.batchSave(x);
                });
            }
            log.info("netty节点:{}服务器端新建连接上报处理成功,共处理{}条", dto.getIntranetIp(), adder.intValue());
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void serverDisconnect(ServerDisconnectDto dto) {
        executor.execute(() -> {
            serverDisconnect(dto, ActionType.SERVER_DISCONNECT);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void serverErrorDisconnect(ServerDisconnectDto dto) {
        executor.execute(() -> {
            serverDisconnect(dto, ActionType.SERVER_ERROR_DISCONNECT);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void serverHeartbeatDisconnect(ServerDisconnectDto dto) {
        executor.execute(() -> {
            serverDisconnect(dto, ActionType.SERVER_HEARTBEAT_DISCONNECT);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shutdown(ServerShutdownDto dto) {
        executor.execute(() -> {
            final Server server = checkServer(dto.getIntranetIp(), dto.getPort());
            serverService.markServerShutdown(server);
            val vo = new ServerVo();
            vo.setId(server.getId());
            redisTool.markServerDown(Collections.singletonList(vo));
            deleteConnections(server);
            log.info("netty节点:{}正常停机处理成功", dto.getIntranetIp());
        });
    }

    @Override
    public void deleteConnections(Server server) {
        List<ClientConnection> clientConnections = clientConnectionService.getListByServerId(server.getId());
        if (!clientConnections.isEmpty()) {
            Map<String, List<ClientConnectionHistory>> clientMap = new HashMap<>(32);
            List<String> clientChannelIds = new ArrayList<>();
            clientConnections.forEach(clientConnection -> {
                val tableName = getTableName(clientConnection.getUserId());
                clientMap.compute(tableName, (k, v) -> {
                    if (Objects.isNull(v)) {
                        v = new ArrayList<>();
                    }
                    v.add(new ClientConnectionHistory(null, clientConnection.getServerId(), clientConnection.getClusterId(), server.getIntranetIp(),
                            clientConnection.getChannelId(), clientConnection.getChannelKey(), clientConnection.getDeviceId(), clientConnection.getUserId(),
                            clientConnection.getRemoteIp(), ActionType.CLIENT_DISCONNECT, new Date()));
                    return v;
                });
                clientChannelIds.add(clientConnection.getChannelId());
            });
            clientMap.forEach((k, v) -> {
                if (!v.isEmpty()) {
                    Lists.partition(v, batchSize).forEach(x -> clientConnectionHistoryService.batchSave(k, x));
                }
            });
            Lists.partition(clientChannelIds, batchSize)
                    .forEach(x -> clientConnectionService.batchDelete(x));
        }
        List<ServerConnection> serverConnections = serverConnectionService.getListByServerId(server.getId());
        if (!serverConnections.isEmpty()) {
            List<ServerConnectionHistory> serverConnectionHistories = new ArrayList<>();
            List<String> serverChannelIds = new ArrayList<>();
            serverConnections.forEach(serverConnection -> {
                val serverConnectionHistory = new ServerConnectionHistory(null, serverConnection.getServerId(), serverConnection.getClusterId(), server.getIntranetIp(),
                        serverConnection.getChannelId(), serverConnection.getChannelKey(), serverConnection.getRemoteIp(), ActionType.SERVER_DISCONNECT, new Date());
                serverConnectionHistories.add(serverConnectionHistory);
                serverChannelIds.add(serverConnection.getChannelId());
            });
            Lists.partition(serverConnectionHistories, batchSize).forEach(x -> serverConnectionHistoryService.batchSave(x));
            Lists.partition(serverChannelIds, batchSize).forEach(x -> serverConnectionService.batchDelete(x));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void connectionHistoryDel() {
        String defaultTableName = GlobalConstant.DEFAULT_LOG_TABLE_NAME_PREFIX;
        int finalClearDays = Math.min(clearDays, TaskConfig.SERVER_CONNECTION_REMOVE_DAYS);
        val delEndTime = com.netty100.utils.DateUtils.formatYMDPlusDay(finalClearDays, com.netty100.utils.DateUtils.YMD_DASH_BLANK_DAY_END_COLON);
        IntStream.range(0, GlobalConstant.DEFAULT_LOG_TABLE_COUNT).forEach(index -> {
            CompletableFuture.runAsync(() -> {
                String tableName = defaultTableName.concat(String.valueOf(index));
                log.info("tableName,{}", tableName);
                List<Integer> clearIds;
                do {
                    clearIds = clientConnectionHistoryService.selectClearIds(tableName, delEndTime, GlobalConstant.DEFAULT_READ_PAGE_SIZE);
                    if (CollectionUtil.isNotEmpty(clearIds)) {
                        clientConnectionHistoryService.deleteConnectionLogs(tableName, clearIds);
                    }
                } while (CollectionUtil.isNotEmpty(clearIds));
            }, executor);
        });
    }

    private void serverDisconnect(ServerDisconnectDto dto, int actionType) {
        Server server = checkServer(dto.getIntranetIp(), dto.getPort());
        val serverConnectionHistoryList = new ArrayList<ServerConnectionHistory>();
        val channelIds = new ArrayList<String>();
        val adder = new LongAdder();
        dto.getProperties().forEach(property -> {
            adder.increment();
            serverConnectionHistoryList.add(new ServerConnectionHistory(null, server.getId(), server.getClusterId(), dto.getIntranetIp(),
                    property.getChannelId(), property.getChannelKey(), property.getRemoteIp(), actionType, property.getDisConnectTime()));
            channelIds.add(property.getChannelId());

        });
        if (!serverConnectionHistoryList.isEmpty()) {
            Lists.partition(serverConnectionHistoryList, batchSize).forEach(x -> {
                serverConnectionHistoryService.batchSave(x);
            });
        }
        if (!channelIds.isEmpty()) {
            Lists.partition(channelIds, batchSize).forEach(x -> {
                serverConnectionService.batchDelete(x);
            });
        }
        log.info("netty节点:{}服务器端连接断开上报处理成功,共处理{}条", dto.getIntranetIp(), adder.intValue());
    }


    private static String getTableName(Long userId) {
        val tableNamePrefix = "api_connection_history";
        return tableNamePrefix + Math.abs(Objects.hash(userId)) % 10;
    }

    private void clientDisconnect(ClientDisconnectDto dto, int actionType) {
        Server server = checkServer(dto.getIntranetIp(), dto.getPort());
        val clientDisconnectionHistoryMap = new HashMap<String, List<ClientConnectionHistory>>(16);
        val userIds = new ArrayList<Long>();
        val channelIds = new ArrayList<String>();
        val adder = new LongAdder();
        dto.getProperties().forEach(property -> {
            adder.increment();
            val tableName = getTableName(property.getUserId());
            val clientConnectionHistory = new ClientConnectionHistory(null, server.getId(), server.getClusterId(), dto.getIntranetIp(), property.getChannelId(), property.getChannelKey(), property.getDeviceId(), property.getUserId(),
                    property.getRemoteIp(), actionType, property.getDisConnectTime());
            clientDisconnectionHistoryMap.compute(tableName, (k, v) -> {
                if (Objects.isNull(v)) {
                    v = new ArrayList<>();
                }
                v.add(clientConnectionHistory);
                return v;
            });
            userIds.add(property.getUserId());
            channelIds.add(property.getChannelId());
        });
        clientDisconnectionHistoryMap.forEach((k, v) -> {
            if (!v.isEmpty()) {
                Lists.partition(v, batchSize).forEach(x -> {
                    clientConnectionHistoryService.batchSave(k, x);
                });
            }
        });
        if (!userIds.isEmpty()) {
            Lists.partition(userIds, batchSize).forEach(x -> {
                activeUserLocationService.batchDelete(x);
            });
        }
        if (!channelIds.isEmpty()) {
            Lists.partition(channelIds, batchSize).forEach(x -> {
                clientConnectionService.batchDelete(x);
            });
        }
        log.info("netty节点:{}游戏端连接断开上报处理成功,共处理{}条", dto.getIntranetIp(), adder.intValue());
    }

    private Server checkServer(String intranetIp, String port) {
        Server server = serverService.getOne(intranetIp, port);
        if (Objects.isNull(server)) {
            log.error("server:{}不存在", intranetIp);
            throw new CommonException(ResponseMsg.SERVER_NOT_EXISTS);
        }
        return server;
    }

    @Autowired
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setReportMinuteDataService(ReportMinuteDataService reportMinuteDataService) {
        this.reportMinuteDataService = reportMinuteDataService;
    }

    @Autowired
    public void setIpTransformer(IpTransformer ipTransformer) {
        this.ipTransformer = ipTransformer;
    }

    @Autowired
    public void setServerConnectionHistoryService(ServerConnectionHistoryService serverConnectionHistoryService) {
        this.serverConnectionHistoryService = serverConnectionHistoryService;
    }

    @Autowired
    public void setClientConnectionHistoryService(ClientConnectionHistoryService clientConnectionHistoryService) {
        this.clientConnectionHistoryService = clientConnectionHistoryService;
    }

    @Autowired
    public void setClientConnectionService(ClientConnectionService clientConnectionService) {
        this.clientConnectionService = clientConnectionService;
    }

    @Autowired
    public void setServerConnectionService(ServerConnectionService serverConnectionService) {
        this.serverConnectionService = serverConnectionService;
    }

    @Autowired
    public void setHistoricalUserLocationService(HistoricalUserLocationService historicalUserLocationService) {
        this.historicalUserLocationService = historicalUserLocationService;
    }

    @Autowired
    public void setActiveUserLocationService(ActiveUserLocationService activeUserLocationService) {
        this.activeUserLocationService = activeUserLocationService;
    }

    @Autowired
    public void setRedisTool(RedisTool redisTool) {
        this.redisTool = redisTool;
    }
}

