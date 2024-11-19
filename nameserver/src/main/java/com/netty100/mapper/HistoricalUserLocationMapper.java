package com.netty100.mapper;

import com.netty100.entity.UserLocation;
import com.netty100.pojo.vo.UserLocationVo;


import java.util.List;

/**
 * @author why
 */
public interface HistoricalUserLocationMapper {

    void batchSave(List<UserLocation> x);

    List<UserLocationVo> getData();

}
