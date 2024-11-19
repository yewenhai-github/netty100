package com.netty100.service;

import com.netty100.entity.UserLocation;
import com.netty100.pojo.vo.UserLocationVo;

import java.util.List;

/**
 * @author why
 */
public interface ActiveUserLocationService {

    void batchSave(List<UserLocation> list);

    void batchDelete(List<Long> userIds);

    List<UserLocationVo> getData();

}
