package com.netty100.controller.admin;


import com.netty100.pojo.dto.AddServerDto;
import com.netty100.service.ServerService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
@Slf4j
@Validated
@RestController(value = "admin-server")
@RequestMapping("/admin/node")
@RequiredArgsConstructor
@Api(tags = "【页面功能-管理端-节点管理】")
public class ServerController {

    private final ServerService serverService;

    /**
     * 手动添加server
     */
    @ApiOperation(value = "添加")
    @PostMapping(value = "/add")
    public WebResult<?> addServer(@Validated @RequestBody AddServerDto dto) {
        serverService.addServer(dto);
        return WebResult.ok();
    }


    @ApiOperation(value = "删除")
    @PostMapping(value = "/delete")
    public WebResult<?> deleteServer(Integer id) {
        serverService.deleteById(id);
        return WebResult.ok();
    }
}
