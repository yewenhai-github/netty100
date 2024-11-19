package com.netty100.service;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.Protocol;
import com.netty100.pojo.dto.AddProtocolDto;
import com.netty100.pojo.dto.ProtocolPageDto;
import com.netty100.pojo.dto.UpdateProtocolDto;

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
public interface ProtocolService {

   Map<String, List<Protocol>> getAll();

   int save(Protocol messageProtocol);

   int update(UpdateProtocolDto dto);

   int deleteById(Integer id);

   Protocol getOne(String protocolType,String protocolCode,String protocolName);

   PageInfo<Protocol> getPage(ProtocolPageDto dto);

    void addProtocol(AddProtocolDto dto);
}
