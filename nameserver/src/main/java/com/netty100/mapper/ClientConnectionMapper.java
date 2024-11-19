package com.netty100.mapper;

import com.netty100.entity.ClientConnection;
import com.netty100.pojo.dto.ConnectionQueryDto;
import com.netty100.pojo.vo.ClientConnectionVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author why
 */
public interface ClientConnectionMapper {

    List<ClientConnectionVo> getList(ConnectionQueryDto dto);

    void batchSave(List<ClientConnection> x);

    void batchDelete(List<String> channelIds);

    List<ClientConnection> getListByServerId(Integer id);

    void batchSaveOrUpdate(List<ClientConnection> clientConnections);

    int deleteInvalidConnections(@Param(value = "timeLimit") Date timeLimit);
}
