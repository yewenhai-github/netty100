package com.netty100.service.impl;


import com.netty100.config.constant.GlobalConstant;
import com.netty100.entity.WarnConfig;
import com.netty100.enumeration.WarnType;
import com.netty100.mapper.WarnConfigMapper;
import com.netty100.pojo.dto.UpdateWarnConfigDto;
import com.netty100.service.WarnConfigService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarnConfigServiceImpl implements WarnConfigService {

    private final WarnConfigMapper warnConfigMapper;
    // 服务节点报警配置
    private ConcurrentMap<Integer/* serverId */, List<WarnConfig>> CONFIG_TABLE = new ConcurrentHashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdateWarnConfigDto dto) {
        return warnConfigMapper.update(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(WarnConfig warnConfig) {
        return warnConfigMapper.insertSelective(warnConfig);
    }

    @Override
    public int deleteById(Integer warnConfigId) {
        return warnConfigMapper.deleteById(warnConfigId);
    }

    @Override
    public List<WarnConfig> getWarnConfig(Integer serverId) {
        List<WarnConfig> configs = CONFIG_TABLE.get(serverId);
        if (CollectionUtil.isNotEmpty(configs)) {
            return configs;
        }
        // 未配置预警参数，走默认值
        configs = CONFIG_TABLE.get(0);
        if (CollectionUtil.isNotEmpty(configs)) {
            return configs;
        }
        // 如果还为空 查db  一般情况下不会走到查询db步骤  做个兼容
        List<WarnConfig> warnConfigs = queryAll();
        List<WarnConfig> defaultWarnConfigs = new ArrayList<>();
        for (WarnConfig warn : warnConfigs) {
            Integer id = warn.getServerId();
            if (Objects.equals(id, serverId)) {
                configs.add(warn);
            }
            if (Objects.equals(id, 0)) {
                defaultWarnConfigs.add(warn);
            }
        }
        if (CollectionUtil.isNotEmpty(configs)) {
            return configs;
        }
        if (CollectionUtil.isNotEmpty(defaultWarnConfigs)) {
            return defaultWarnConfigs;
        }
        return null;
    }

    @Override
    public List<WarnConfig> queryAll() {
        return warnConfigMapper.queryAll();
    }

    @Override
    public void setConfigTable(List<WarnConfig> warnConfigs) {
        CONFIG_TABLE.clear();
        Map<Integer, List<WarnConfig>> map = warnConfigs.stream().collect(Collectors.groupingBy(WarnConfig::getServerId));
        CONFIG_TABLE.putAll(map);
    }

    @Override
    public void init() {
        for (WarnType warnType : WarnType.values()) {
            // 初始化时 如果group为空 意味不用设置阈值
            if (StringUtils.isEmpty(warnType.getGroup())) {
                continue;
            }
            WarnConfig warnConfig = new WarnConfig();
            warnConfig.setServerId(GlobalConstant.DEFAULT_SERVER_ID);
            warnConfig.setTypeIndex(warnType.getIndex());
            warnConfig.setTypeGroup(warnType.getGroup());
            warnConfig.setTypeThreshold(BigDecimal.ZERO);
            warnConfig.setTypeTitle(warnType.getTitle().
                    replace(GlobalConstant.INIT_REPLACE_WORD, GlobalConstant.STING_EMPTY));
            warnConfigMapper.insertSelective(warnConfig);
        }
    }

    @Override
    public Map<String, List<WarnConfig>> getList0(Integer serverId) {
        final List<WarnConfig> res = warnConfigMapper.getList0(serverId);
        val map = new HashMap<String, List<WarnConfig>>(16);
        res.forEach(warnConfig -> map.compute(warnConfig.getTypeGroup(), (k, v) -> {
            if (Objects.isNull(v)) {
                v = new ArrayList<>();
            }
            v.add(warnConfig);
            return v;
        }));
        return map;
    }

    @Override
    public WarnConfig getOne(String typeGroup, String typeTitle, Integer serverId) {
        return warnConfigMapper.getOne(typeGroup, typeTitle, serverId);
    }

    @Override
    public WarnConfig getById(Integer id) {
        return warnConfigMapper.getById(id);
    }
}
