package com.netty100.controller.admin;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.RegistrationDetail;
import com.netty100.pojo.dto.RegistrationDetailQueryDto;
import com.netty100.service.RegistrationDetailService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author why
 */
@Slf4j
@Validated
@RestController(value = "admin-registrationDetail")
@RequestMapping(value = "/admin/device")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "【页面功能-管理端-设备注册码】")
public class RegistrationDetailController {

    private final RegistrationDetailService registrationDetailService;

    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/query/page")
    public WebResult<PageInfo<RegistrationDetail>> getPage(@RequestBody RegistrationDetailQueryDto dto) {
        return WebResult.ok(registrationDetailService.getPage(dto));
    }

    @ApiOperation(value = "更新", notes = "仅支持按照记录id更新设备码")
    @PostMapping(value = "/update")
    public WebResult<Boolean> update(@RequestBody RegistrationDetail registrationDetail) {
        return WebResult.ok(registrationDetailService.updateRegistrationDetail(registrationDetail));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/delete/{id}")
    public WebResult<Boolean> delete(@PathVariable Long id) {
        return WebResult.ok(registrationDetailService.deleteById(id));
    }
}