package com.netty100.mapper;

import com.netty100.entity.AppConfig;
import com.netty100.pojo.dto.AppConfigQueryDto;
import com.netty100.pojo.vo.AppConfigVo;

import java.util.List;

/**
 * @author why
 */
public interface AppConfigMapper {

    boolean save(AppConfig appConfig);

    boolean update(AppConfig appConfig);

    boolean deleteById(Long id);

    List<AppConfigVo> getList(AppConfigQueryDto dto);

    List<AppConfig> getAll();
}
