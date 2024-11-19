package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.AccessLog;
import com.netty100.pojo.dto.AccessLogPageQueryDto;
import com.netty100.pojo.vo.AccessLogPvLineChartVo;
import com.netty100.pojo.vo.AccessLogUvLineChartVo;

import java.util.Date;
import java.util.List;

/**
 * @author why
 */
public interface AccessLogService {

    int save(AccessLog accessLog);

    List<AccessLogPvLineChartVo> getPvLineChartData();

    List<AccessLogUvLineChartVo> getUvLineChartData();

    PageInfo<AccessLog> getPage(AccessLogPageQueryDto dto);

    void deleteInvalidAccessLog(Date timeLimit);
}
