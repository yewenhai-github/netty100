package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.AppConfig;
import com.netty100.entity.Protocol;
import com.netty100.mapper.AppConfigMapper;
import com.netty100.pojo.dto.AppConfigQueryDto;
import com.netty100.pojo.vo.AppConfigVo;
import com.netty100.pojo.vo.ClientChannelLogVo;
import com.netty100.pojo.vo.ClientConnectionVo;
import com.netty100.service.AppConfigService;
import com.netty100.service.ProtocolService;
import com.netty100.utils.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 应用协议配置添加缓存功能,
 *
 * @author why
 */
@Slf4j
@Service
public class AppConfigServiceImpl implements AppConfigService {

    private AppConfigMapper appConfigMapper;

    private ProtocolService protocolService;

    @Autowired
    public void setProtocolService(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    @Autowired
    public void setAppConfigMapper(AppConfigMapper appConfigMapper) {
        this.appConfigMapper = appConfigMapper;
    }


    @Override
    public boolean save(AppConfig appConfig) {
        try {
            return appConfigMapper.save(appConfig);
        } catch (Exception e) {
            log.error("AppConfigMapper save error", e);
            throw new CommonException("该消息源和消息目的地组合配置已存在");
        }
    }

    @Override
    public boolean update(AppConfig appConfig) {
        try {
            return appConfigMapper.update(appConfig);
        } catch (Exception e) {
            log.error("AppConfigMapper update error", e);
            throw new CommonException("该消息源和消息目的地组合配置已存在");
        }
    }

    @Override
    public boolean deleteById(Long id) {
        return appConfigMapper.deleteById(id);
    }

    private List<AppConfigVo> getList(AppConfigQueryDto dto) {
        return appConfigMapper.getList(dto);
    }

    @Override
    public PageInfo<AppConfigVo> getPage(AppConfigQueryDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        val appName = dto.getAppName();
        val cluster = dto.getCluster();
        if (StringUtils.hasText(appName)) {
            dto.setAppName("%" + appName.trim() + "%");
        }
        if (StringUtils.hasText(cluster)) {
            dto.setCluster("%" + cluster.trim() + "%");
        }
        val page = new PageInfo<>(this.getList(dto));
        val list = page.getList();
        transform(list);
        return page;
    }

    private void transform(List<AppConfigVo> list) {
        Map<String, List<Protocol>> map = protocolService.getAll();
        List<Protocol> messageSources = map.get("message_source");
        List<Protocol> messageDests = map.get("message_dest");
        List<Protocol> messageSerializes = map.get("message_serialize");
        list.forEach(element -> {
            element.setMessageSourceDesc(getDesc(element.getMessageSource(), messageSources));
            element.setMessageDestDesc(getDesc(element.getMessageDest(), messageDests));
            element.setMessageSerializeDesc(getDesc(element.getMessageSerialize(), messageSerializes));
        });
    }

    private String getDesc(Byte code, List<Protocol> protocols) {
        if (Objects.isNull(protocols)) {
            return "";
        }
        String desc = String.valueOf(code);
        final Optional<Protocol> option = protocols.stream().filter(x -> Objects.equals(x.getProtocolCode(), desc)).findFirst();
        return option.isPresent() ? option.get().getProtocolName() : "";
    }

    @Override
    public List<AppConfig> getAll() {
        return appConfigMapper.getAll();
    }
}
