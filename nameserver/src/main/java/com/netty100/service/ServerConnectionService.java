package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.ServerConnection;
import com.netty100.pojo.dto.ConnectionQueryDto;
import com.netty100.pojo.vo.ServerConnectionVo;

import java.util.Date;
import java.util.List;

/**
 * @author why
 */
public interface ServerConnectionService {

    PageInfo<ServerConnectionVo> getPage(ConnectionQueryDto dto);

    void batchSave(List<ServerConnection> serverConnections);

    void batchDelete(List<String> channelIds);

    List<ServerConnection> getListByServerId(Integer id);

    void batchSaveOrUpdate(List<ServerConnection> serverConnections);

    int deleteInvalidConnections(Date timeLimit);
}
