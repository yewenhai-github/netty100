package com.netty100.mapper;


import com.netty100.entity.UserLocation;
import com.netty100.pojo.vo.UserLocationVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author why
 */
public interface ActiveUserLocationMapper {

    void batchSave(List<UserLocation> list);

    void batchDelete(@Param("userIds") List<Long> userIds);

    List<UserLocationVo> getData();

}
