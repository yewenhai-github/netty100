package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.Protocol;
import com.netty100.entity.ServerConnection;
import com.netty100.mapper.ServerConnectionMapper;
import com.netty100.pojo.dto.ConnectionQueryDto;
import com.netty100.pojo.vo.ServerConnectionVo;
import com.netty100.service.ProtocolService;
import com.netty100.service.ServerConnectionService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author why
 */
@SuppressWarnings("DuplicatedCode")
@Service
public class ServerConnectionServiceImpl implements ServerConnectionService {

    private ServerConnectionMapper serverConnectionMapper;

    private ProtocolService protocolService;

    @Autowired
    public void setProtocolService(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @Autowired
    public void setServerConnectionMapper(ServerConnectionMapper serverConnectionMapper) {
        this.serverConnectionMapper = serverConnectionMapper;
    }

    @Override
    public PageInfo<ServerConnectionVo> getPage(ConnectionQueryDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        val page = new PageInfo<>(serverConnectionMapper.getList(dto));
        final List<ServerConnectionVo> list = page.getList();
        transform(list);
        return page;
    }

    private void transform(List<ServerConnectionVo> list) {
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

    @Override
    public void batchSave(List<ServerConnection> serverConnections) {
        serverConnectionMapper.batchSave(serverConnections);
    }

    @Override
    public void batchDelete(List<String> channelIds) {
        serverConnectionMapper.batchDelete(channelIds);
    }

    @Override
    public List<ServerConnection> getListByServerId(Integer serverId) {
        return serverConnectionMapper.getListByServerId(serverId);
    }

    @Override
    public void batchSaveOrUpdate(List<ServerConnection> serverConnections) {
        serverConnectionMapper.batchSaveOrUpdate(serverConnections);
    }

    @Override
    public int deleteInvalidConnections(Date timeLimit) {
       return serverConnectionMapper.deleteInvalidConnections(timeLimit);
    }
}
