package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.ClientConnection;
import com.netty100.pojo.dto.ConnectionQueryDto;
import com.netty100.pojo.vo.ClientConnectionVo;

import java.util.Date;
import java.util.List;

/**
 * @author why
 */
public interface ClientConnectionService {

    PageInfo<ClientConnectionVo> getPage(ConnectionQueryDto dto);

    void batchSave(List<ClientConnection> x);

    void batchDelete(List<String> channelIds);

    List<ClientConnection> getListByServerId(Integer id);

    void batchSaveOrUpdate(List<ClientConnection> clientConnections);

    int deleteInvalidConnections(Date timeLimit);
}
