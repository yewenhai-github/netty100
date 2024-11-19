package com.netty100.service;

import com.netty100.entity.UserCluster;

import java.util.List;

/**
 * @author why
 */
public interface UserClusterService {

    void assign(List<UserCluster> list);


    int delete(Integer clusterId);

    List<Integer> getClusterIdsByUserId(Integer userId);

    List<UserCluster> getAll();

    void deleteUser(Integer userId);
}
