package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.netty100.entity.Message;
import com.netty100.entity.Protocol;
import com.netty100.entity.Server;
import com.netty100.mapper.MessageMapper;
import com.netty100.pojo.dto.MessageQueryDto;
import com.netty100.pojo.vo.MessageLogPageQueryVo;
import com.netty100.service.MessageService;
import com.netty100.service.ProtocolService;
import com.netty100.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author why
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private ServerService serverService;

    private MessageMapper messageMapper;

    private ProtocolService protocolService;

    private final LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    private DataProcessor dataProcessor;

    @Autowired
    public void setProtocolService(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @Autowired
    public void setMessageMapper(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Autowired
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    @PostConstruct
    public void postConstruct() {
        val threadName = "message-consumer";
        dataProcessor = new DataProcessor(threadName, serverService, messageMapper, queue, true);
        dataProcessor.start();
        log.info("消息日志消费线程:{}成功启动", threadName);
    }

    @PreDestroy
    public void preDestroy() {
        dataProcessor.shutdown();
        log.info("消息日志消费线程:{}停止", dataProcessor.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<Message> messages) {
        if (messages.isEmpty()) {
            return;
        }
        queue.addAll(messages);
    }

    private static class DataProcessor extends Thread {
        private static final Logger threadLog = LoggerFactory.getLogger(DataProcessor.class);
        private final ServerService serverService;
        private final MessageMapper messageMapper;
        private final LinkedBlockingQueue<Message> queue;
        private volatile boolean flag;

        public DataProcessor(String name, ServerService serverService, MessageMapper messageMapper, LinkedBlockingQueue<Message> queue, boolean flag) {
            super(name);
            this.serverService = serverService;
            this.messageMapper = messageMapper;
            this.queue = queue;
            this.flag = flag;
        }

        public void shutdown() {
            this.flag = false;
        }

        @Override
        public void run() {
            while (flag) {
                val messages = new ArrayList<Message>();
                val num = queue.drainTo(messages);
                if (Objects.equals(num, 0)) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ignore) {
                        //ignore the exception
                    }
                } else {
                    Message dto = messages.get(0);
                    try {
                        final Server server = serverService.getOne(dto.getLocalAddress(), dto.getLocalPort());
                        if (Objects.isNull(server)) {
                            if (Objects.nonNull(threadLog)) {
                                threadLog.warn("找不到指定的netty节点,IP地址:{}", dto.getLocalAddress());
                            }
                        } else {
                            messages.forEach(message -> {
                                message.setServerId(server.getId());
                                message.setClusterId(server.getClusterId());
                            });
                            final int batchSize = 500;
                            Lists.partition(messages, batchSize).forEach(messageMapper::batchSave);
                            threadLog.info("netty节点:{}消息日志上报处理成功,共处理{}条", server.getIntranetIp(), messages.size());
                            messages.clear();
                        }
                    } catch (Exception e) {
                        threadLog.error("netty节点:{}消息日志上报处理失败", dto.getLocalAddress(), e);
                    }
                }
            }
        }
    }

    @Override
    public PageInfo<Message> getPage(MessageQueryDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        if (StringUtils.hasText(dto.getRemoteAddress())) {
            String ip = "%" + dto.getRemoteAddress().trim() + "%";
            dto.setRemoteAddress(ip);
        }
        val page = new PageInfo<>(messageMapper.getList(dto));
        val list = page.getList();
        transform(list);
        return page;
    }

    @Override
    public long getDistinctMessageIdCount(MessageQueryDto dto) {
        return messageMapper.getDistinctMessageIdCount(dto);
    }

    @Override
    public List<String> getMessageIdList(MessageQueryDto dto) {
        return messageMapper.getMessageIdList(dto);
    }

    @Override
    public List<Message> getByMessageIds(List<String> messageIds, MessageQueryDto dto) {
        return messageMapper.getByMessageIds(messageIds, dto);
    }

    @Override
    public MessageLogPageQueryVo page(MessageQueryDto dto) {
        val vo = new MessageLogPageQueryVo();
        long count = messageMapper.getDistinctMessageIdCount(dto);
        if (count == 0L) {
            vo.setCount(0L);
            vo.setResult(null);
            return vo;
        }
        List<String> messageIds = messageMapper.getMessageIdList(dto);
        List<Message> messages = messageMapper.getByMessageIds(messageIds, dto);
        transform(messages);
        vo.setCount(count);
        vo.setResult(split(messages, dto));
        return vo;
    }

    @Override
    public void deleteExpiredLog(Date date) {
        messageMapper.deleteExpiredLog(date);
    }

    @Override
    public Map<String, Long> queryMaxMinId(Date date) {
        return messageMapper.queryMaxMinId(date);
    }

    @Override
    public int deleteExpiredLogById(long maxId) {
        return messageMapper.deleteExpiredLogById(maxId);
    }

    private List<List<Message>> split(List<Message> messages, MessageQueryDto dto) {
        if (Objects.nonNull(dto.getLogPoint())) {
            messages = messages.stream().filter(x -> x.getLeaf().intValue() == 0 || x.getLogPoint().equals(dto.getLogPoint())).collect(Collectors.toList());
        }
        final Map<Long, List<Message>> map = messages.stream().collect(Collectors.groupingBy(Message::getMessageId));
        //        map.values().forEach(x -> x.sort(Comparator.comparingInt(Message::getLeaf)));
        val result = new ArrayList<>(map.values());
        result.sort((a, b) -> (int) (b.get(0).getLogTime().getTime() - a.get(0).getLogTime().getTime()));
        return result;
    }

    private void transform(List<Message> list) {
        Map<String, List<Protocol>> map = protocolService.getAll();
        List<Protocol> messageTypes = map.get("message_type");
        List<Protocol> messageSources = map.get("message_source");
        List<Protocol> messageDests = map.get("message_dest");
        List<Protocol> messageWays = map.get("message_way");
        List<Protocol> messageSerializes = map.get("message_serialize");
        list.forEach(element -> {
            element.setMessageWayDesc(getDesc(element.getMessageWay(), messageWays));
            element.setMessageTypeDesc(getDesc(element.getMessageType(), messageTypes));
            element.setMessageSourceDesc(getDesc(element.getMessageSource(), messageSources));
            element.setMessageDestDesc(getDesc(element.getMessageDest(), messageDests));
            element.setMessageSerializeDesc(getDesc(element.getMessageSerialize(), messageSerializes));
        });
    }

    private String getDesc(Byte code, List<Protocol> protocols) {
        if (Objects.isNull(protocols)) {
            return "";
        }
        String desc = String.valueOf(code);
        final Optional<Protocol> option = protocols.stream().filter(x -> Objects.equals(x.getProtocolCode(), desc)).findFirst();
        return option.isPresent() ? option.get().getProtocolName() : "";
    }
}
