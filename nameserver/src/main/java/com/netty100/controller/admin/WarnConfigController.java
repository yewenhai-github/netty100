package com.netty100.controller.admin;


import com.netty100.entity.WarnConfig;
import com.netty100.enumeration.WarnType;
import com.netty100.pojo.dto.UpdateWarnConfigDto;
import com.netty100.service.WarnConfigService;
import com.netty100.utils.WebResult;
import com.netty100.utils.respons.ResponseMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
@Api(tags = "【页面功能-管理端-告警阈值管理】")
public class WarnConfigController {

    private final WarnConfigService warnConfigService;

    @ApiOperation(value = "获取类型列表")
    @PostMapping(value = "/type/list")
    public WebResult<Map<String, List<WarnType>>> getTypeList() {
        final List<WarnType> types = Arrays.stream(WarnType.values()).filter(x -> StringUtils.hasText(x.getGroup())).collect(Collectors.toList());
        val map = new HashMap<String, List<WarnType>>(32);
        types.forEach(warnType -> map.compute(warnType.getGroup(), (k, v) -> {
            if (Objects.isNull(v)) {
                v = new ArrayList<>();
            }
            v.add(warnType);
            return v;
        }));
        map.remove("");
        return WebResult.ok(map);
    }

    @ApiOperation(value = "获取列表")
    @PostMapping(value = "/list/{serverId}")
    public WebResult<Map<String, List<WarnConfig>>> getList(@PathVariable(value = "serverId") Integer serverId) {
        return WebResult.ok(warnConfigService.getList0(serverId));
    }

    @ApiOperation(value = "更新")
    @PostMapping(value = "/update")
    public WebResult<?> updateConfig(@Valid @RequestBody UpdateWarnConfigDto dto) {
        try {
            warnConfigService.update(dto);
            return WebResult.ok();
        } catch (Exception e) {
            return WebResult.error(ResponseMsg.CONFIG_ALREADY_EXISTS);
        }
    }

    @ApiOperation(value = "添加")
    @PostMapping(value = "/add")
    public WebResult<?> addConfig(@RequestBody WarnConfig warnConfig) {
        try {
            warnConfigService.save(warnConfig);
            return WebResult.ok();
        } catch (Exception e) {
            return WebResult.error(ResponseMsg.CONFIG_ALREADY_EXISTS);
        }
    }

    @ApiOperation(value = "删除")
    @PostMapping(value = "/delete/{id}")
    public WebResult<?> delete(@PathVariable(value = "id") Integer id) {
        WarnConfig warnConfig = warnConfigService.getById(id);
        if (Objects.equals(warnConfig.getServerId(), 0)) {
            return WebResult.error(ResponseMsg.DEFAULT_CONFIG_DELETE);
        }
        warnConfigService.deleteById(id);
        return WebResult.ok();
    }

    @ApiOperation(value = "初始化全局告警")
    @PostMapping(value = "/init")
    public WebResult<?> init() {
        warnConfigService.init();
        return WebResult.ok();
    }
}
