package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.Server;
import com.netty100.pojo.dto.*;
import com.netty100.pojo.vo.ServerBriefVo;
import com.netty100.pojo.vo.ServerListVo;
import com.netty100.pojo.vo.ServerVo;

import java.util.List;
import java.util.Set;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
@SuppressWarnings("ALL")
public interface ServerService {

    void bootReport(BootReportDto dto);

    int save(Server server);

    List<ServerVo> getServers(Integer clusterId);

    List<Server> findDead(int timeout, String nowTime);

    List<Integer> getClusterServerStatus(Integer clusterId);

    int deleteById(Integer serverId);

    Server getOne(String intranetIp, String port);

    void reportConnectionCount(ReportConnectionCountDto dto);

    int addServer(AddServerDto dto);

    int update(Server server);

    void reportMessage(ReportMessageDto dto);

    Server getById(Integer serverId);

    List<Server> findOpenMonitorServers();

    List<ServerVo> getServers0(Integer id);

    int getUpServerCountByClusterId(Integer clusterId);

    int getTotalServerCountByClusterId(Integer clusterId);

    PageInfo<ServerListVo> getServerPage(ServerListDto dto);

    List<ServerBriefVo> briefServerList(Integer clusterId);

    void markServerShutdown(Server server);

    List<ServerVo> getAllActiveServers();

    Set<ServerVo> getServerVoSetByIds(List<Integer> ids);
}
