package com.netty100.service.impl;

import com.netty100.entity.UserLocation;
import com.netty100.mapper.HistoricalUserLocationMapper;
import com.netty100.pojo.vo.UserLocationVo;
import com.netty100.service.HistoricalUserLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author why
 */
@Service
public class HistoricalUserLocationServiceImpl implements HistoricalUserLocationService {

    private HistoricalUserLocationMapper historicalUserLocationMapper;

    @Autowired
    public void setHistoricalUserLocationMapper(HistoricalUserLocationMapper historicalUserLocationMapper) {
        this.historicalUserLocationMapper = historicalUserLocationMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<UserLocation> x) {
        historicalUserLocationMapper.batchSave(x);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLocationVo> getData() {
        return historicalUserLocationMapper.getData();
    }
}
