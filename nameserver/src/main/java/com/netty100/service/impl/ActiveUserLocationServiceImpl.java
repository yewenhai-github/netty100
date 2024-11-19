package com.netty100.service.impl;

import com.netty100.entity.UserLocation;
import com.netty100.mapper.ActiveUserLocationMapper;
import com.netty100.pojo.vo.UserLocationVo;
import com.netty100.service.ActiveUserLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author why
 */
@Service
public class ActiveUserLocationServiceImpl implements ActiveUserLocationService {

    private ActiveUserLocationMapper activeUserLocationMapper;

    @Autowired
    public void setActiveUserLocationMapper(ActiveUserLocationMapper activeUserLocationMapper) {
        this.activeUserLocationMapper = activeUserLocationMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<UserLocation> list) {
        activeUserLocationMapper.batchSave(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> userIds) {
        activeUserLocationMapper.batchDelete(userIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLocationVo> getData() {
        return activeUserLocationMapper.getData();
    }
}
