package com.netty100.service;




import com.netty100.entity.UserLocation;
import com.netty100.pojo.vo.UserLocationVo;

import java.util.List;

/**
 * @author why
 */
public interface HistoricalUserLocationService {


    void batchSave(List<UserLocation> x);

    List<UserLocationVo> getData();

}
