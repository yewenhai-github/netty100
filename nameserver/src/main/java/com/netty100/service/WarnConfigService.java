package com.netty100.service;


import com.netty100.entity.WarnConfig;
import com.netty100.pojo.dto.UpdateWarnConfigDto;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface WarnConfigService {
    List<WarnConfig> getWarnConfig(Integer serverId);

    int update(UpdateWarnConfigDto dto);

    int save(WarnConfig warnConfig);

    int deleteById(Integer warnConfigId);

    List<WarnConfig> queryAll();

    void setConfigTable(List<WarnConfig> warnConfigs);

    void init();

    Map<String,List<WarnConfig>> getList0(Integer serverId);

    WarnConfig getOne(String typeGroup, String typeTitle,Integer serverId);

    WarnConfig getById(Integer id);
}
