package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.KernelMessageLog;
import com.netty100.pojo.dto.KernelMessageLogQueryDto;
import com.netty100.pojo.vo.KernelMessageLogVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author why
 */
public interface KernelMessageLogService {

    void batchSave(List<KernelMessageLog> data);

    PageInfo<KernelMessageLogVo> getPage(KernelMessageLogQueryDto dto);

    void deleteExpiredLog(Date date);

    Map<String, Long> queryMaxMinId(Date date);

    int deleteExpiredLogById(long maxId);
}
