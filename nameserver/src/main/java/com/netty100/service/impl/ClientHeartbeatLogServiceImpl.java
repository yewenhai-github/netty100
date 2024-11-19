package com.netty100.service.impl;

import com.google.common.collect.Lists;
import com.netty100.entity.ClientConnection;
import com.netty100.entity.ClientHeartbeatLog;
import com.netty100.service.ClientConnectionService;
import com.netty100.service.ClientHeartbeatLogService;
import com.netty100.service.ServerService;
import com.netty100.utils.exception.CommonException;
import com.netty100.utils.respons.ResponseMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author why
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientHeartbeatLogServiceImpl implements ClientHeartbeatLogService {

    private final ClientConnectionService clientConnectionService;

    private final ServerService serverService;

    private final Map<String, LinkedBlockingQueue<ClientHeartbeatLog>> map = new ConcurrentHashMap<>();

    private final List<DataProcessor> consumers = new ArrayList<>();

    private final AtomicInteger integer = new AtomicInteger(0);

    @Value(value = "${client.heartbeat.log.consumer.threads.num:1}")
    private int threadsNum;

    @PreDestroy
    public void preDestroy() {
        if (!consumers.isEmpty()) {
            consumers.forEach(DataProcessor::shutdown);
            log.info("游戏端心跳日志上报处理线程停止");
        }
    }

    @Override
    public void processData(List<ClientHeartbeatLog> data) {
        if (!data.isEmpty()) {
            val entity = data.get(0);
            val server = serverService.getOne(entity.getLocalAddress(), entity.getLocalPort());
            if (Objects.isNull(server)) {
                throw new CommonException(ResponseMsg.SERVER_NOT_EXISTS);
            }
            map.compute(server.getId() + "-" + server.getClusterId(), (k, v) -> {
                if (Objects.isNull(v)) {
                    v = new LinkedBlockingQueue<>();
                    for (int i = 0; i < threadsNum; i++) {
                        val threadName = "client-heartbeat-log-consumer-" + integer.incrementAndGet();
                        val dataProcessor = new DataProcessor(entity.getLocalAddress(), k, v, clientConnectionService, true, threadName);
                        dataProcessor.start();
                        log.info("新增游戏端心跳日志处理线程,线程名称:{},上报数据节点地址:{}", threadName, server.getIntranetIp());
                        consumers.add(dataProcessor);
                    }
                }
                v.addAll(data);
                return v;
            });
            log.info("游戏端心跳日志上报{}条,数据上报节点:{}", entity.getLocalAddress(), data.size());
        }
    }


    public static class DataProcessor extends Thread {

        private final String nettyAddress;

        private final String key;

        private final LinkedBlockingQueue<ClientHeartbeatLog> queue;

        private final ClientConnectionService clientConnectionService;

        private volatile boolean isRunning;

        public DataProcessor(String nettyAddress, String key, LinkedBlockingQueue<ClientHeartbeatLog> queue, ClientConnectionService clientConnectionService, boolean isRunning, String name) {
            super(name);
            this.nettyAddress = nettyAddress;
            this.key = key;
            this.queue = queue;
            this.clientConnectionService = clientConnectionService;
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            val list = new ArrayList<ClientHeartbeatLog>();
            while (isRunning) {
                int size = queue.drainTo(list);
                if (size == 0) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        //ignore
                    }
                } else {
                    final List<ClientConnection> clientConnections = transformTo(key, list);
                    list.clear();
                    val batchSize = 500;
                    try {
                        Lists.partition(clientConnections, batchSize).forEach(clientConnectionService::batchSaveOrUpdate);
                        log.info("成功处理游戏端连接心跳日志{}条,数据上报节点:{}", clientConnections.size(), nettyAddress);
                    } catch (Exception e) {
                        log.error("游戏端心跳日志上报处理失败,数据上报节点:{}", nettyAddress, e);
                    }
                }
            }
            log.info("游戏端心跳日志处理线程停止,线程名称:{},数据上报节点:{}",getName(),nettyAddress);
        }

        private List<ClientConnection> transformTo(String key, List<ClientHeartbeatLog> data) {
            val list = new ArrayList<ClientConnection>();
            val arr = key.split("-");
            Integer serverId = Integer.parseInt(arr[0]);
            Integer clusterId = Integer.parseInt(arr[1]);
            data.forEach(x -> list.add(new ClientConnection(null, serverId, clusterId, x.getLocalAddress(), x.getChannelId(), x.getChannelKey(), x.getDeviceId(),
                    x.getUserId(),
                    x.getMessageWay(), x.getMessageSource(), x.getMessageDest(), x.getMessageType(), x.getMessageSerialize(), x.getRemoteAddress(), x.getIdleTime(), x.getIdleTime())));
            return list;
        }

        public void shutdown() {
            this.isRunning = false;
        }
    }
}
