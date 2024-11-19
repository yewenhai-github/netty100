package com.netty100.service;

import com.netty100.pojo.dto.MonitorDto;
import com.netty100.pojo.vo.MonitorResultVo;

/**
 * @author why
 */
public interface MonitoringAlarmsService {

    MonitorResultVo getMonitorData(MonitorDto dto);
}
