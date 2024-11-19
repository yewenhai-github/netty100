package com.netty100.mapper;

import com.netty100.entity.ClientChannelLog;
import com.netty100.pojo.dto.ClientChannelLogQueryDto;
import com.netty100.pojo.vo.ClientChannelLogVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author why
 */
public interface ClientChannelLogMapper {

    void batchSave(List<ClientChannelLog> data);

    List<ClientChannelLogVo> getList(ClientChannelLogQueryDto dto);

    void deleteExpiredLog(Date date);

    Map<String, Long> queryMaxMinId(Date date);

    /**
     * 根据ID区间删除数据
     * @param minId 最小ID
     * @param maxId 最大ID
     * @return
     */
    int deleteExpiredLogById(@Param("maxId") long maxId);
}
