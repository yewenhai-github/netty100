package com.netty100.service;

import com.netty100.entity.Cluster;
import com.netty100.entity.Server;
import com.netty100.enumeration.WarnType;
import com.netty100.pojo.dto.ServerWarnContentDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/30
 */
public interface CommonWarnSendService {
    void warnServerSend(List<ServerWarnContentDto> servers, WarnType warnType, List<String> times);

    void warnClusterSend(List<Cluster> clusters, WarnType warnType);

    ServerWarnContentDto warpWarn(Server server, BigDecimal threshold, BigDecimal check, WarnType warnType);
}
