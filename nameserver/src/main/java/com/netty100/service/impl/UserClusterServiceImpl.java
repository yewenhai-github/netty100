package com.netty100.service.impl;

import com.netty100.entity.UserCluster;
import com.netty100.mapper.UserClusterMapper;
import com.netty100.service.UserClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author why
 */
@Service
public class UserClusterServiceImpl implements UserClusterService {

    private UserClusterMapper userClusterMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(List<UserCluster> list) {
        userClusterMapper.assign(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer clusterId) {
        return userClusterMapper.delete(clusterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getClusterIdsByUserId(Integer userId) {
        return userClusterMapper.getClusterIdsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCluster> getAll() {
        return userClusterMapper.list();
    }

    @Override
    public void deleteUser(Integer userId) {
        userClusterMapper.deleteUser(userId);
    }

    @Autowired
    public void setUserClusterMapper(UserClusterMapper userClusterMapper) {
        this.userClusterMapper = userClusterMapper;
    }
}
