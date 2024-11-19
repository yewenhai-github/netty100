package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.RegistrationDetail;
import com.netty100.pojo.dto.RegistrationDetailQueryDto;

/**
 * @author why
 */
public interface RegistrationDetailService {

    String getDevicePwd(Long  deviceId,Long userId);

    boolean updateRegistrationDetail(RegistrationDetail registrationDetail);

    boolean deleteById(Long id);

    PageInfo<RegistrationDetail> getPage(RegistrationDetailQueryDto dto);

    String getDevicePwdByNode(Long deviceId, Long userId);
}
