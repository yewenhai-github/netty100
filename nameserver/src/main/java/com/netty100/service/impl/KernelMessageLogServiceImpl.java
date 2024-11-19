package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.netty100.entity.KernelMessageLog;
import com.netty100.entity.Protocol;
import com.netty100.mapper.KernelMessageLogMapper;
import com.netty100.pojo.dto.KernelMessageLogQueryDto;
import com.netty100.pojo.vo.KernelMessageLogVo;
import com.netty100.service.KernelMessageLogService;
import com.netty100.service.ProtocolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author why
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KernelMessageLogServiceImpl implements KernelMessageLogService {

    private final KernelMessageLogMapper kernelMessageLogMapper;

    private final ProtocolService protocolService;

    @Override
    @Async
    public void batchSave(List<KernelMessageLog> data) {
        if (!data.isEmpty()) {
            val batchSize = 500;
            Lists.partition(data, batchSize).forEach(kernelMessageLogMapper::batchSave);
            log.info("内核日志上报处理成功,总处理{}条,上报数据节点:{}", data.size(), data.get(0).getLocalAddress());
        }

    }

    private List<KernelMessageLogVo> getList(KernelMessageLogQueryDto dto) {
        if (Objects.isNull(dto.getStartTime()) && Objects.nonNull(dto.getEndTime())) {
            dto.setStartTime(DateUtils.addDays(dto.getEndTime(), -1));
        }
        if (Objects.nonNull(dto.getStartTime()) && Objects.isNull(dto.getEndTime())) {
            dto.setEndTime(DateUtils.addDays(dto.getStartTime(), 1));
        }
        return kernelMessageLogMapper.getList(dto);
    }

    @Override
    public PageInfo<KernelMessageLogVo> getPage(KernelMessageLogQueryDto dto) {
        if (!StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        }
        if (StringUtils.hasText(dto.getLocalAddress())) {
            dto.setLocalAddress("%" + dto.getLocalAddress().trim() + "%");
        }
        val page = new PageInfo<>(getList(dto));
        val list = page.getList();
        transform(list);
        return page;
    }

    @Override
    public void deleteExpiredLog(Date date) {
        kernelMessageLogMapper.deleteExpiredLog(date);
    }

    @Override
    public Map<String, Long> queryMaxMinId(Date date) {
        return kernelMessageLogMapper.queryMaxMinId(date);
    }

    @Override
    public int deleteExpiredLogById(long maxId) {
        return kernelMessageLogMapper.deleteExpiredLogById(maxId);
    }


    private void transform(List<KernelMessageLogVo> list) {
        Map<String, List<Protocol>> map = protocolService.getAll();
        List<Protocol> messageSources = map.get("message_source");
        List<Protocol> messageDests = map.get("message_dest");
        list.forEach(element -> {
            element.setMessageSourceDesc(getDesc(element.getMessageSource(), messageSources));
            element.setMessageDestDesc(getDesc(element.getMessageDest(), messageDests));
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
}
