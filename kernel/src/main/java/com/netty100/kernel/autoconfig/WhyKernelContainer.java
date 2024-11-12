package com.netty100.kernel.autoconfig;

import com.netty100.kernel.annotation.EnableMessageType;
import com.netty100.kernel.annotation.EnableMessageWay;
import com.netty100.kernel.processor.RejectHandler;
import com.netty100.kernel.processor.RequestPair;
import com.netty100.kernel.processor.RequestProcessor;
import com.netty100.kernel.processor.ThreadFactoryImpl;
import com.netty100.kernel.processor.service.server.MessageTypeService;
import com.netty100.common.constants.CommonConstants;
import com.netty100.common.utils.WhySpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/5/25
 * @since 1.0.0, 2022/5/25
 */
@Slf4j
public class WhyKernelContainer {
    public static ConcurrentMap<Byte, MessageTypeService> handlerRepository = new ConcurrentHashMap<Byte, MessageTypeService>();
    private static ConcurrentMap<Byte, RequestPair<RequestProcessor, ExecutorService>> processorTable = new ConcurrentHashMap<>();
    private static HashMap<Integer, ExecutorService> executorMap = new HashMap<>();

    private ExecutorService connectExecutor;
    private ExecutorService clientExecutor;
    private ExecutorService serverExecutor;
    private ExecutorService pingExecutor;

    private LinkedBlockingQueue<Runnable> connectQueue;
    private LinkedBlockingQueue<Runnable> clientQueue;
    private LinkedBlockingQueue<Runnable> serverQueue;
    private LinkedBlockingQueue<Runnable> pingQueue;

    public void initHandlerRepository() {
        this.connectQueue = new LinkedBlockingQueue<>(100000);
        this.clientQueue = new LinkedBlockingQueue<>(100000);
        this.serverQueue = new LinkedBlockingQueue<>(100000);
        this.pingQueue = new LinkedBlockingQueue<>(10000);

        int coreThreadNum = Runtime.getRuntime().availableProcessors();
        this.connectExecutor = new ThreadPoolExecutor(coreThreadNum, coreThreadNum, 60000, TimeUnit.MILLISECONDS, connectQueue, new ThreadFactoryImpl("connectThread"), new RejectHandler("connect", 100000));
        this.clientExecutor = new ThreadPoolExecutor(coreThreadNum, coreThreadNum, 60000, TimeUnit.MILLISECONDS, clientQueue, new ThreadFactoryImpl("clientThread"), new RejectHandler("client", 100000));
        this.serverExecutor = new ThreadPoolExecutor(coreThreadNum, coreThreadNum, 60000, TimeUnit.MILLISECONDS, serverQueue, new ThreadFactoryImpl("serverThread"), new RejectHandler("server", 100000));
        this.pingExecutor = new ThreadPoolExecutor(coreThreadNum, coreThreadNum, 60000, TimeUnit.MILLISECONDS, pingQueue, new ThreadFactoryImpl("pingThread"), new RejectHandler("heartbeat", 100000));
        executorMap.put(CommonConstants.CONNECT_EXECUTOR, connectExecutor);
        executorMap.put(CommonConstants.CLIENT_EXECUTOR, clientExecutor);
        executorMap.put(CommonConstants.SERVER_EXECUTOR, serverExecutor);
        executorMap.put(CommonConstants.PING_EXECUTOR, pingExecutor);

        Map<String, Object> serviceBeanMap = WhySpringUtils.getApplicationContext().getBeansWithAnnotation(EnableMessageWay.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof RequestProcessor) {
                    EnableMessageWay enableMessageWay = serviceBean.getClass().getAnnotation(EnableMessageWay.class);
                    byte messageWay = enableMessageWay.value();
                    int executor = enableMessageWay.executor();
                    if (loadRequestPair(messageWay) != null) {
                        throw new RuntimeException("kernel handler[" + messageWay + "] naming conflicts.");
                    }
                    RequestProcessor processor = (RequestProcessor) serviceBean;
                    this.processorTable.put(messageWay, new RequestPair<>(processor, executorMap.get(executor)));

//                    log.info(">>>>>>>>>>> kernel register handler success, messageWay:{}, processor:{}, executor:{}", messageWay, processor, executorMap.get(executor));
                }
            }
        }

        Map<String, Object> serviceBeanMap2 = WhySpringUtils.getApplicationContext().getBeansWithAnnotation(EnableMessageType.class);
        if (serviceBeanMap2 != null && serviceBeanMap2.size() > 0) {
            for (Object serviceBean : serviceBeanMap2.values()) {
                if (serviceBean instanceof MessageTypeService) {
                    EnableMessageType enableMessageType = serviceBean.getClass().getAnnotation(EnableMessageType.class);
                    byte[] messageTypes = enableMessageType.value();
                    for (int i = 0; i < messageTypes.length; i++) {
                        byte messageType = messageTypes[i];
                        if (loadHandler(messageType) != null) {
                            throw new RuntimeException("kernel handler[" + messageType + "] naming conflicts.");
                        }
                        MessageTypeService handler = (MessageTypeService) serviceBean;
                        handlerRepository.put(messageType, handler);
//                        log.info(">>>>>>>>>>> kernel register handler success, messageType:{}, Handler:{}", messageType, handler);
                    }
                }
            }
        }
    }

    public static RequestPair loadRequestPair(byte messageWay){
        return processorTable.get(messageWay);
    }

    public static MessageTypeService loadHandler(byte name){
        return handlerRepository.get(name);
    }


}
