package com.netty100.mapper;

import com.netty100.entity.ServerConnection;
import com.netty100.pojo.dto.ConnectionQueryDto;
import com.netty100.pojo.vo.ServerConnectionVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author why
 */
public interface ServerConnectionMapper {

    List<ServerConnectionVo> getList(ConnectionQueryDto dto);

    void batchSave(List<ServerConnection> serverConnections);

    void batchDelete(List<String> channelIds);

    List<ServerConnection> getListByServerId(Integer serverId);

    void batchSaveOrUpdate(List<ServerConnection> serverConnections);

    int deleteInvalidConnections(@Param(value = "timeLimit") Date timeLimit);
}
