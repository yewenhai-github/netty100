package com.netty100.mapper;

import com.netty100.entity.Server;
import com.netty100.pojo.vo.ServerBriefVo;
import com.netty100.pojo.vo.ServerListVo;
import com.netty100.pojo.vo.ServerVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
public interface ServerMapper {

    int save(Server server);

    List<ServerVo> getServers(Integer clusterId);

    List<Server> findDead(@Param("timeout") int timeout, @Param("nowTime") String nowTime);

    List<Server> findOpenMonitorServers();

    List<Integer> getClusterServerStatus(@Param("clusterId") Integer clusterId);

    int deleteById(Integer serverId);

    Server getOne(@Param("intranetIp") String intranetIp, @Param("port") String port);

    int update(Server server);

    Server getById(Integer serverId);

    List<ServerVo> getServers0(Integer id);

    int updateLastHeartBeatTime(Server server);

    int getUpServerCountByClusterId(Integer clusterId);

    int getTotalServerCountByClusterId(Integer clusterId);

    List<ServerListVo> getServerList(Integer clusterId);

    List<ServerBriefVo> briefServerList(Integer clusterId);

    void markServerShutdown(Server server);

    List<ServerVo> getAllActiveServers();

    Set<ServerVo> getServerVoSetByIds(List<Integer> ids);
}
