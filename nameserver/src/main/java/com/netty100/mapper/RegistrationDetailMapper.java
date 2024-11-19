package com.netty100.mapper;

import com.netty100.entity.RegistrationDetail;
import com.netty100.pojo.dto.RegistrationDetailQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author why
 */
public interface RegistrationDetailMapper {

    String getDevicePwd(@Param(value = "deviceId") Long deviceId, @Param(value = "userId") Long userId);

    boolean save(RegistrationDetail registrationDetail);

    boolean updateRegistrationDetail(RegistrationDetail registrationDetail);

    boolean deleteById(Long id);

    List<RegistrationDetail> getList(RegistrationDetailQueryDto dto);

    RegistrationDetail getById(Long id);
}
