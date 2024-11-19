package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.netty100.entity.ClientChannelLog;
import com.netty100.entity.Cluster;
import com.netty100.entity.Protocol;
import com.netty100.mapper.ClientChannelLogMapper;
import com.netty100.pojo.dto.ClientChannelLogQueryDto;
import com.netty100.pojo.vo.ClientChannelLogVo;
import com.netty100.service.ClientChannelLogService;
import com.netty100.service.ClusterService;
import com.netty100.service.ProtocolService;
import com.netty100.service.ServerService;
import com.netty100.utils.exception.CommonException;
import com.netty100.utils.respons.ResponseMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author why
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientChannelLogServiceImpl implements ClientChannelLogService {

    private final ClientChannelLogMapper clientChannelLogMapper;

    private final ServerService serverService;

    private final ClusterService clusterService;

    private final ThreadPoolTaskExecutor taskExecutor;

    private final ProtocolService protocolService;

    @Override
    public void batchSave(List<ClientChannelLog> data) {
        if (data.isEmpty()) {
            return;
        }
        val entity = data.get(0);
        val server = serverService.getOne(entity.getLocalAddress(), entity.getLocalPort());
        if (Objects.isNull(server)) {
            throw new CommonException(ResponseMsg.SERVER_NOT_EXISTS);
        }
        final Cluster cluster = clusterService.getById(server.getClusterId());
        if (Objects.isNull(cluster)) {
            throw new CommonException(ResponseMsg.CLUSTER_NOT_EXIST);
        }
        val clusterName = cluster.getCluster();
        final int batchSize = 500;
        val adder = new LongAdder();
        taskExecutor.execute(() -> {
            data.forEach(x -> {
                adder.increment();
                x.setServerId(server.getId());
                x.setClusterId(server.getClusterId());
                x.setClusterName(clusterName);
            });
            Lists.partition(data, batchSize).forEach(clientChannelLogMapper::batchSave);
            log.info("客户端连接日志上报处理成功,共处理{}条数据", adder.intValue());
        });
    }

    @Override
    public List<ClientChannelLogVo> getList(ClientChannelLogQueryDto dto) {
        if (Objects.isNull(dto.getStartTime()) && Objects.nonNull(dto.getEndTime())) {
            dto.setStartTime(DateUtils.addDays(dto.getEndTime(), -1));
        }
        if (Objects.nonNull(dto.getStartTime()) && Objects.isNull(dto.getEndTime())) {
            dto.setEndTime(DateUtils.addDays(dto.getStartTime(), 1));
        }
        return clientChannelLogMapper.getList(dto);
    }

    @Override
    public PageInfo<ClientChannelLogVo> getPage(ClientChannelLogQueryDto dto) {
        val remoteAddress = dto.getRemoteAddress();
        if (StringUtils.hasText(remoteAddress)) {
            dto.setRemoteAddress("%" + remoteAddress.trim() + "%");
        }
        if(StringUtils.hasText(dto.getLocalAddress())){
            dto.setLocalAddress("%"+dto.getLocalAddress().trim()+"%");
        }
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        val page = new PageInfo<>(getList(dto));
        val result = page.getList();
        transform(result);
        return page;
    }

    @Override
    public void deleteExpiredLog(Date date) {
        clientChannelLogMapper.deleteExpiredLog(date);
    }

    @Override
    public Map<String, Long> queryMaxMinId(Date date) {
        return clientChannelLogMapper.queryMaxMinId(date);
    }

    @Override
    public int deleteExpiredLogById(long idEnd) {
        return clientChannelLogMapper.deleteExpiredLogById(idEnd);
    }

    private void transform(List<ClientChannelLogVo> list) {
        Map<String, List<Protocol>> map = protocolService.getAll();
        List<Protocol> messageSources = map.get("message_source");
        List<Protocol> messageDests = map.get("message_dest");
        list.forEach(element -> {
            element.setMessageSourceDesc(getDesc(element.getMessageSource(), messageSources));
            element.setMessageDestDesc(getDesc(element.getMessageDest(), messageDests));
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
