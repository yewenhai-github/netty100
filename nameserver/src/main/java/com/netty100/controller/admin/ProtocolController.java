package com.netty100.controller.admin;


import com.github.pagehelper.PageInfo;
import com.netty100.entity.Protocol;
import com.netty100.enumeration.ProtocolType;
import com.netty100.pojo.dto.AddProtocolDto;
import com.netty100.pojo.dto.ProtocolPageDto;
import com.netty100.pojo.dto.UpdateProtocolDto;
import com.netty100.service.ProtocolService;
import com.netty100.utils.WebResult;
import com.netty100.utils.respons.ResponseMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Validated
@RestController
@RequestMapping("/admin/protocol")
@RequiredArgsConstructor
@Api(tags = "【页面功能-管理端-消息协议管理】")
public class ProtocolController {

    private final ProtocolService protocolService;

    @ApiOperation(value = "新增")
    @PostMapping(value = "/add")
    public WebResult<?> add(@Valid @RequestBody AddProtocolDto dto) {
        try {
            protocolService.addProtocol(dto);
            return WebResult.ok();
        } catch (Exception e) {
            return WebResult.error(ResponseMsg.PROTOCOL_VALUE_EXIST);
        }
    }


    /**
     * 只能修改code,name,desc不能修改type
     */
    @ApiOperation(value = "修改")
    @PostMapping(value = "/update")
    public WebResult<?> update(@Valid @RequestBody UpdateProtocolDto dto) {
        try {
            protocolService.update(dto);
            return WebResult.ok();
        } catch (Exception e) {
            return WebResult.error(ResponseMsg.PROTOCOL_VALUE_EXIST);
        }
    }

    @ApiOperation(value = "删除")
    @PostMapping(value = "/delete/{id}")
    public WebResult<?> delete(@PathVariable(value = "id") Integer id) {
        protocolService.deleteById(id);
        return WebResult.ok();
    }

    @ApiOperation(value = "获取消息协议类型")
    @PostMapping(value = "/type/list")
    public WebResult<Map<String, String>> getTypes() {
        val map = new HashMap<String, String>(8);
        map.put("message_way", ProtocolType.MESSAGE_WAY);
        map.put("message_type", ProtocolType.MESSAGE_TYPE);
        map.put("message_source", ProtocolType.MESSAGE_SOURCE);
        map.put("message_dest", ProtocolType.MESSAGE_DEST);
        map.put("message_serialize", ProtocolType.MESSAGE_SERIALIZE);
        return WebResult.ok(map);
    }

    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/page")
    public WebResult<PageInfo<Protocol>> getPage(@RequestBody ProtocolPageDto dto) {
        return WebResult.ok(protocolService.getPage(dto));
    }

    @ApiOperation(value = "获取协议列表")
    @PostMapping(value = "/list")
    public WebResult<Map<String, List<Protocol>>> list() {
        return WebResult.ok(protocolService.getAll());
    }

}
