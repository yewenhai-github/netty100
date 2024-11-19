package com.netty100.mapper;

import com.netty100.entity.KernelMessageLog;
import com.netty100.pojo.dto.KernelMessageLogQueryDto;
import com.netty100.pojo.vo.KernelMessageLogVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author why
 */
public interface KernelMessageLogMapper {

    void batchSave(List<KernelMessageLog> data);

    List<KernelMessageLogVo> getList(KernelMessageLogQueryDto dto);

    void deleteExpiredLog(Date date);

    Map<String, Long> queryMaxMinId(Date date);

    int deleteExpiredLogById(long maxId);
}
