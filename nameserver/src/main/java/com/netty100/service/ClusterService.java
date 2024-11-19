package com.netty100.service;

import com.netty100.entity.Cluster;
import com.netty100.pojo.dto.AddClusterDto;
import com.netty100.pojo.dto.AssignClusterToUserDto;
import com.netty100.pojo.dto.UpdateClusterDto;
import com.netty100.pojo.vo.ClusterVo;
import com.netty100.pojo.vo.UserClusterVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */

public interface ClusterService {

    int save(Cluster cluster);

    int update(UpdateClusterDto dto);

    Cluster getById(Integer clusterId);

    void assign(AssignClusterToUserDto dto);

    Cluster getByClusterName(String clusterName);

    List<ClusterVo> getClusters();

    List<Cluster> getClusterInfo();

    List<Cluster> queryAll();

    List<UserClusterVo> getUserClusters();

    void addCluster(AddClusterDto dto);

    void updateCluster(UpdateClusterDto dto);

    List<Integer> getByUserId(Integer userId);

    List<Integer> getIds();

}
