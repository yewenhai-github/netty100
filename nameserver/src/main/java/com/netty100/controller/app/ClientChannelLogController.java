package com.netty100.controller.app;

import com.github.pagehelper.PageInfo;
import com.netty100.pojo.dto.ClientChannelLogQueryDto;
import com.netty100.pojo.vo.ClientChannelLogVo;
import com.netty100.service.ClientChannelLogService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author why
 */
@Slf4j
@RestController
@RequestMapping(value = "/app/client-channel")
@Api(tags = "【页面功能-用户端-连接日志】")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientChannelLogController {

    private final ClientChannelLogService clientChannelLogService;

    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/query")
    public WebResult<PageInfo<ClientChannelLogVo>> getPage(@RequestBody ClientChannelLogQueryDto dto) {
        return WebResult.ok(clientChannelLogService.getPage(dto));
    }
}
