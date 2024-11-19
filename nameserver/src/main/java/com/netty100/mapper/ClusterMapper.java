package com.netty100.mapper;

import com.netty100.entity.Cluster;
import com.netty100.pojo.dto.UpdateClusterDto;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
public interface ClusterMapper {

    int save(Cluster cluster);

    int update(UpdateClusterDto dto);

    Cluster getById(Integer clusterId);

    Cluster getByClusterName(String clusterName);

    List<Cluster> getAll();

    List<Cluster> getByIds(List<Integer> clusterIds);

    List<Integer> getIds();

}

