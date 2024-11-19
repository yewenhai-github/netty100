package com.netty100.service.impl;

import com.google.common.collect.Lists;
import com.netty100.entity.ServerConnection;
import com.netty100.entity.ServerHeartBeatLog;
import com.netty100.service.ServerConnectionService;
import com.netty100.service.ServerHeartbeatLogService;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author why
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServerHeartbeatLogServiceImpl implements ServerHeartbeatLogService {

    private final ServerConnectionService serverConnectionService;

    private final ServerService serverService;

    private final Map<String, LinkedBlockingQueue<ServerHeartBeatLog>> map = new ConcurrentHashMap<>();

    private final List<DataProcessor> consumers = new CopyOnWriteArrayList<>();

    private final AtomicInteger integer = new AtomicInteger(0);

    @Value(value = "${server.heartbeat.log.consumer.threads.num:1}")
    private int threadsNum;

    @PreDestroy
    public void preDestroy() {
        if (!consumers.isEmpty()) {
            consumers.forEach(DataProcessor::shutdown);
            log.info("服务器端心跳日志上报处理线程停止");
        }
    }

    @Override
    public void processData(List<ServerHeartBeatLog> data) {
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
                        val threadName = "server-heartbeat-log-consumer-" + integer.incrementAndGet();
                        val dataProcessor = new DataProcessor(entity.getLocalAddress(), k, v, serverConnectionService, true, threadName);
                        dataProcessor.start();
                        log.info("新增服务器端端心跳日志处理线程,线程名称:{},上报数据节点地址:{}", threadName, server.getIntranetIp());
                        consumers.add(dataProcessor);
                    }
                }
                v.addAll(data);
                return v;
            });
            log.info("服务器端心跳日志上报{}条,数据上报节点:{}", data.size(), entity.getLocalAddress());
        }
    }


    public static class DataProcessor extends Thread {

        private final String nettyAddress;

        private final String key;

        private final LinkedBlockingQueue<ServerHeartBeatLog> queue;

        private final ServerConnectionService serverConnectionService;

        private volatile boolean isRunning;

        public DataProcessor(String nettyAddress, String key, LinkedBlockingQueue<ServerHeartBeatLog> queue, ServerConnectionService serverConnectionService, boolean isRunning, String name) {
            super(name);
            this.nettyAddress = nettyAddress;
            this.key = key;
            this.queue = queue;
            this.serverConnectionService = serverConnectionService;
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            val list = new ArrayList<ServerHeartBeatLog>();
            while (isRunning) {
                int size = queue.drainTo(list);
                if (size == 0) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        //ignore
                    }
                } else {
                    final List<ServerConnection> serverConnections = transformTo(key, list);
                    list.clear();
                    val batchSize = 500;
                    try {
                        Lists.partition(serverConnections, batchSize).forEach(serverConnectionService::batchSaveOrUpdate);
                        log.info("成功处理服务器端连接心跳日志{}条,数据上报节点:{}", serverConnections.size(), nettyAddress);
                    } catch (Exception e) {
                        log.error("服务器端心跳日志上报处理失败,数据上报节点:{}", nettyAddress, e);
                    }
                }
            }
            log.info("服务器端心跳日志处理线程停止,线程名称:{},数据上报节点:{}", getName(), nettyAddress);
        }

        private List<ServerConnection> transformTo(String key, List<ServerHeartBeatLog> data) {
            val list = new ArrayList<ServerConnection>();
            val arr = key.split("-");
            Integer serverId = Integer.parseInt(arr[0]);
            Integer clusterId = Integer.parseInt(arr[1]);
            data.forEach(x -> list.add(new ServerConnection(null, serverId, clusterId, x.getLocalAddress(), x.getChannelId(), x.getChannelKey(),
                    x.getMessageWay(), x.getMessageSource(), x.getMessageDest(), x.getMessageType(), x.getMessageSerialize(),
                    x.getRemoteAddress(), x.getIdleTime(), x.getIdleTime())));
            return list;
        }

        public void shutdown() {
            this.isRunning = false;
        }
    }
}
