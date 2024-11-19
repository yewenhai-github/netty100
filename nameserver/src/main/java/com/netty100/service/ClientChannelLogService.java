package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.ClientChannelLog;
import com.netty100.pojo.dto.ClientChannelLogQueryDto;
import com.netty100.pojo.vo.ClientChannelLogVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author why
 */
public interface ClientChannelLogService {

    void batchSave(List<ClientChannelLog> data);

    List<ClientChannelLogVo> getList(ClientChannelLogQueryDto dto);

    PageInfo<ClientChannelLogVo> getPage(ClientChannelLogQueryDto dto);

    void deleteExpiredLog(Date date);

    Map<String, Long> queryMaxMinId(Date date);

    int deleteExpiredLogById(long tmpIdEnd);
}
