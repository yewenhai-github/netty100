package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.AppConfig;
import com.netty100.pojo.dto.AppConfigQueryDto;
import com.netty100.pojo.vo.AppConfigVo;

import java.util.List;

/**
 * @author why
 */
public interface AppConfigService {

    boolean save(AppConfig appConfig);

    boolean update(AppConfig appConfig);

    boolean deleteById(Long id);

    PageInfo<AppConfigVo> getPage(AppConfigQueryDto dto);

    List<AppConfig> getAll();
}
