package com.netty100.mapper;

import com.netty100.entity.Message;
import com.netty100.pojo.dto.MessageQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MessageMapper {

    void batchSave(List<Message> x);

    List<Message> getList(MessageQueryDto dto);

    long getDistinctMessageIdCount(MessageQueryDto dto);

    List<String> getMessageIdList(MessageQueryDto dto);

    List<Message> getByMessageIds(@Param("messageIds") List<String> messageIds, @Param("dto") MessageQueryDto dto);

    void deleteExpiredLog(Date date);

    Map<String, Long> queryMaxMinId(Date date);

    int deleteExpiredLogById(long maxId);
}
