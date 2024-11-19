package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.ClientConnection;
import com.netty100.entity.Protocol;
import com.netty100.mapper.ClientConnectionMapper;
import com.netty100.pojo.dto.ConnectionQueryDto;
import com.netty100.pojo.vo.ClientConnectionVo;
import com.netty100.service.ClientConnectionService;
import com.netty100.service.ProtocolService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author why
 */
@SuppressWarnings("DuplicatedCode")
@Service
public class ClientConnectionServiceImpl implements ClientConnectionService {

    private ClientConnectionMapper clientConnectionMapper;

    private ProtocolService protocolService;

    @Autowired
    public void setProtocolService(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @Autowired
    public void setClientConnectionMapper(ClientConnectionMapper clientConnectionMapper) {
        this.clientConnectionMapper = clientConnectionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<ClientConnectionVo> getPage(ConnectionQueryDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        val page = new PageInfo<>(clientConnectionMapper.getList(dto));
        final List<ClientConnectionVo> list = page.getList();
        transform(list);
        return page;
    }

    private void transform(List<ClientConnectionVo> list) {
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
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<ClientConnection> list) {
        clientConnectionMapper.batchSave(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<String> channelIds) {
        clientConnectionMapper.batchDelete(channelIds);
    }

    @Override
    public List<ClientConnection> getListByServerId(Integer id) {
        return clientConnectionMapper.getListByServerId(id);
    }

    @Override
    public void batchSaveOrUpdate(List<ClientConnection> clientConnections) {
        clientConnectionMapper.batchSaveOrUpdate(clientConnections);
    }

    @Override
    public int deleteInvalidConnections(Date timeLimit) {
       return clientConnectionMapper.deleteInvalidConnections(timeLimit);
    }
}
