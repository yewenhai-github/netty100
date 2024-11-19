package com.netty100.controller.app;

import com.netty100.pojo.dto.OnlineDebugDto;
import com.netty100.service.OnlineDebugService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;


/**
 * @author why
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/app/online-debug")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "【页面功能-帮助手册-在线调试】")
public class OnlineDebuggingController {

    private final OnlineDebugService onlineDebugService;

    @ApiOperation(value = "在线调试")
    @PostMapping(value = "/send-message")
    public WebResult<?> onlineDebug(@RequestBody @Valid OnlineDebugDto dto) {
        if (!StringUtils.hasText(dto.getMessage())) {
            return WebResult.error("调试消息不能为空");
        }
        final String response = onlineDebugService.onlineDebug(dto);
        if (Objects.isNull(response)) {
            return WebResult.error("在线调试消息发送失败");
        }
        return WebResult.ok(response);
    }


    @ApiOperation(value = "创建连接")
    @PostMapping(value = "/connect")
    public WebResult<String> connect(@RequestBody @Valid OnlineDebugDto dto) {
        return WebResult.ok(onlineDebugService.connect(dto));
    }

    @ApiOperation(value = "连接断开")
    @PostMapping(value = "/disconnect")
    public WebResult<String> disconnect(@RequestBody @Valid OnlineDebugDto dto) {
        return WebResult.ok(onlineDebugService.disconnect(dto));
    }
}
