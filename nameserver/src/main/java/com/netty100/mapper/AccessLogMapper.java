package com.netty100.mapper;

import com.netty100.entity.AccessLog;
import com.netty100.pojo.dto.AccessLogPageQueryDto;
import com.netty100.pojo.vo.AccessLogPvLineChartVo;
import com.netty100.pojo.vo.AccessLogUvLineChartVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AccessLogMapper {

    int save(AccessLog accessLog);

    List<AccessLogPvLineChartVo> getPvLineChartData();

    List<AccessLogUvLineChartVo> getUvLineChartData();

    List<AccessLog> getList(AccessLogPageQueryDto dto);

    void deleteInvalidAccessLog(@Param(value = "timeLimit") Date timeLimit);
}
