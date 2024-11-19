package com.netty100.controller.admin;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.AppConfig;
import com.netty100.pojo.dto.AppConfigQueryDto;
import com.netty100.pojo.vo.AppConfigVo;
import com.netty100.service.AppConfigService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author why
 */
@RestController
@RequestMapping(value = "admin/app-config")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "【页面功能-管理端-应用协议配置管理】")
public class ApiConfigController {

    private final AppConfigService appConfigService;

    @ApiOperation(value = "新增")
    @PostMapping(value = "/add")
    public WebResult<Boolean> add(@RequestBody AppConfig appConfig) {
        return WebResult.ok(appConfigService.save(appConfig));
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/remove/{id}")
    public WebResult<Boolean> delete(@PathVariable Long id) {
        return WebResult.ok(appConfigService.deleteById(id));
    }

    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/query/page")
    public WebResult<PageInfo<AppConfigVo>> getPage(@RequestBody AppConfigQueryDto dto) {
        return WebResult.ok(appConfigService.getPage(dto));
    }

    @ApiOperation(value = "更新")
    @PostMapping(value = "/update")
    public WebResult<Boolean> update(@RequestBody AppConfig appConfig){
        return WebResult.ok(appConfigService.update(appConfig));
    }

}
