package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.AccessLog;
import com.netty100.mapper.AccessLogMapper;
import com.netty100.pojo.dto.AccessLogPageQueryDto;
import com.netty100.pojo.vo.AccessLogPvLineChartVo;
import com.netty100.pojo.vo.AccessLogUvLineChartVo;
import com.netty100.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author why
 */
@Service
public class AccessLogServiceImpl implements AccessLogService {

    private AccessLogMapper accessLogMapper;

    @Autowired
    public void setAccessLogMapper(AccessLogMapper accessLogMapper) {
        this.accessLogMapper = accessLogMapper;
    }

    @Override
    public int save(AccessLog accessLog) {
        return accessLogMapper.save(accessLog);
    }

    @Override
    public List<AccessLogPvLineChartVo> getPvLineChartData() {
        return accessLogMapper.getPvLineChartData();
    }

    @Override
    public List<AccessLogUvLineChartVo> getUvLineChartData() {
        return accessLogMapper.getUvLineChartData();
    }

    @Override
    public PageInfo<AccessLog> getPage(AccessLogPageQueryDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        List<AccessLog> list = accessLogMapper.getList(dto);
        return new PageInfo<>(list);
    }

    @Override
    public void deleteInvalidAccessLog(Date timeLimit) {
        accessLogMapper.deleteInvalidAccessLog(timeLimit);
    }
}
