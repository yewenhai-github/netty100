package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.Protocol;
import com.netty100.mapper.MessageProtocolMapper;
import com.netty100.pojo.dto.AddProtocolDto;
import com.netty100.pojo.dto.ProtocolPageDto;
import com.netty100.pojo.dto.UpdateProtocolDto;
import com.netty100.service.ProtocolService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

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
public class ProtocolServiceImpl implements ProtocolService {

    private final MessageProtocolMapper messageProtocolMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<Protocol>> getAll() {
        final List<Protocol> list = messageProtocolMapper.getAll();
        Map<String, List<Protocol>> map = new HashMap<>(16);
        list.forEach(arg -> map.compute(arg.getProtocolType(), (k, v) -> {
            if (Objects.isNull(v)) {
                v = new ArrayList<>();
            }
            v.add(arg);
            return v;
        }));
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(Protocol messageProtocol) {
        return messageProtocolMapper.save(messageProtocol);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdateProtocolDto dto) {
        return messageProtocolMapper.update(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Integer id) {
        return messageProtocolMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Protocol getOne(String protocolType, String protocolCode, String protocolName) {
        return messageProtocolMapper.getOne(protocolType, protocolCode, protocolName);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<Protocol> getPage(ProtocolPageDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        return new PageInfo<>(messageProtocolMapper.getList(dto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProtocol(AddProtocolDto dto) {
        val messageProtocol = new Protocol();
        BeanUtils.copyProperties(dto, messageProtocol);
        this.save(messageProtocol);
    }
}
