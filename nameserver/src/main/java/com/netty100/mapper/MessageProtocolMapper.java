package com.netty100.mapper;

import com.netty100.entity.Protocol;
import com.netty100.pojo.dto.ProtocolPageDto;
import com.netty100.pojo.dto.UpdateProtocolDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
public interface MessageProtocolMapper {

    List<Protocol> getAll();

    int save(Protocol messageProtocol);

    int update(UpdateProtocolDto dto);

    int deleteById(Integer id);

    Protocol getOne(@Param("protocolType") String protocolType, @Param("protocolCode") String protocolCode, @Param("protocolName") String protocolName);

    List<Protocol> getList(ProtocolPageDto dto);
}
