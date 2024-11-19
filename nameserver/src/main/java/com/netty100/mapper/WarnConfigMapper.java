package com.netty100.mapper;

import com.netty100.entity.WarnConfig;
import com.netty100.pojo.dto.UpdateWarnConfigDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface WarnConfigMapper {

    int insertSelective(WarnConfig record);

    int updateByPrimaryKeySelective(WarnConfig record);

    WarnConfig selectByPrimaryKey(Integer id);

    List<WarnConfig> getList();

    List<WarnConfig> getList0(Integer serverId);

    int deleteById(Integer warnConfigId);

    List<WarnConfig> queryAll();

    WarnConfig getOne(@Param("typeGroup") String typeGroup,@Param("typeTitle") String typeTitle,@Param("serverId")Integer serverId);

    int update(UpdateWarnConfigDto dto);

    WarnConfig getById(Integer id);
}
