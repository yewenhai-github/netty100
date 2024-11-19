package com.netty100.mapper;

import com.netty100.entity.UserCluster;


import java.util.List;

/**
 * @author why
 */
public interface UserClusterMapper {

    void assign(List<UserCluster> list);

    int delete(Integer clusterId);

    List<Integer> getClusterIdsByUserId(Integer userId);

    List<UserCluster> list();

    void deleteUser(Integer userId);
}
