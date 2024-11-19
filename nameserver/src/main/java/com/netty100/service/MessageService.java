package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.Message;
import com.netty100.pojo.dto.MessageQueryDto;
import com.netty100.pojo.vo.MessageLogPageQueryVo;


import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author why
 */
public interface MessageService {

    void batchInsert(List<Message> messages);

    PageInfo<Message> getPage(MessageQueryDto dto);

    long getDistinctMessageIdCount(MessageQueryDto dto);

    List<String> getMessageIdList(MessageQueryDto dto);

    List<Message> getByMessageIds(List<String> messageIds,MessageQueryDto dto);

    MessageLogPageQueryVo page(MessageQueryDto dto);

    void deleteExpiredLog(Date date);

    Map<String, Long> queryMaxMinId(Date date);

    int deleteExpiredLogById(long tmpIdEnd);
}
