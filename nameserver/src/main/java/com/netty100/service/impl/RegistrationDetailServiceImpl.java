package com.netty100.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.RegistrationDetail;
import com.netty100.mapper.RegistrationDetailMapper;
import com.netty100.pojo.dto.RegistrationDetailQueryDto;
import com.netty100.service.RegistrationDetailService;
import lombok.val;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author why
 */
@Service
public class RegistrationDetailServiceImpl implements RegistrationDetailService {

    private ExpiringMap<String, String> devicePwdCache;

    private RegistrationDetailMapper registrationDetailMapper;

    @Autowired
    public void setRegistrationDetailMapper(RegistrationDetailMapper registrationDetailMapper) {
        this.registrationDetailMapper = registrationDetailMapper;
    }

    @Autowired
    @Qualifier(value = "devicePwdCache")
    public void setDevicePwdCache(ExpiringMap<String, String> devicePwdCache) {
        this.devicePwdCache = devicePwdCache;
    }

    @Override
    public String getDevicePwd(Long deviceId, Long userId) {
        String key = generateCacheKey(deviceId, userId);
        String devicePwd = devicePwdCache.get(key);
        if (Objects.isNull(devicePwd)) {
            devicePwd = registrationDetailMapper.getDevicePwd(deviceId, userId);
            if (Objects.isNull(devicePwd)) {
                devicePwd = generateDevicePwd();
                val registrationDetail = new RegistrationDetail();
                registrationDetail.setUserId(userId);
                registrationDetail.setDevicePwd(devicePwd);
                registrationDetail.setDeviceId(deviceId);
                registrationDetailMapper.save(registrationDetail);
            }
            devicePwdCache.put(key, devicePwd);
        }
        return devicePwd;
    }

    @Override
    public boolean updateRegistrationDetail(RegistrationDetail registrationDetail) {
        val result = registrationDetailMapper.updateRegistrationDetail(registrationDetail);
        if (result) {
            RegistrationDetail entity = registrationDetailMapper.getById(registrationDetail.getId());
            devicePwdCache.put(generateCacheKey(entity.getDeviceId(), entity.getUserId()), registrationDetail.getDevicePwd());
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        RegistrationDetail entity = registrationDetailMapper.getById(id);
        if (Objects.isNull(entity)) {
            return false;
        }
        final boolean result = registrationDetailMapper.deleteById(id);
        if (result) {
            devicePwdCache.remove(generateCacheKey(entity.getDeviceId(), entity.getUserId()));
        }
        return result;
    }

    @Override
    public PageInfo<RegistrationDetail> getPage(RegistrationDetailQueryDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        return new PageInfo<>(this.getList(dto));
    }

    @Override
    public String getDevicePwdByNode(Long deviceId, Long userId) {
        String key = generateCacheKey(deviceId, userId);
        String devicePwd = devicePwdCache.get(key);
        if (Objects.isNull(devicePwd)) {
            devicePwd = registrationDetailMapper.getDevicePwd(deviceId, userId);
            if (Objects.nonNull(devicePwd)) {
                devicePwdCache.put(key, devicePwd);
            }
        }
        return devicePwd;
    }

    public List<RegistrationDetail> getList(RegistrationDetailQueryDto dto) {
        if (Objects.isNull(dto.getStartTime()) && Objects.nonNull(dto.getEndTime())) {
            dto.setStartTime(DateUtils.addDays(dto.getEndTime(), -1));
        }
        if (Objects.nonNull(dto.getStartTime()) && Objects.isNull(dto.getEndTime())) {
            dto.setEndTime(DateUtils.addDays(dto.getStartTime(), 1));
        }
        return registrationDetailMapper.getList(dto);
    }

    private String generateDevicePwd() {
        return RandomUtil.randomStringUpper(6);
    }

    private String generateCacheKey(Long deviceId, Long userId) {
        return String.valueOf(deviceId).concat("-").concat(String.valueOf(userId));
    }
}
