package com.netty100.controller.app;

import com.github.pagehelper.PageInfo;
import com.netty100.pojo.dto.KernelMessageLogQueryDto;
import com.netty100.pojo.vo.KernelMessageLogVo;
import com.netty100.service.KernelMessageLogService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author why
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/app/kernel/message")
@Api(tags = "【页面功能-用户端-内核日志】")
public class KernelMessageLogController {

    private final KernelMessageLogService kernelMessageLogService;

    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/query/page")
    public WebResult<PageInfo<KernelMessageLogVo>> getPage(@RequestBody KernelMessageLogQueryDto dto) {
        return WebResult.ok(kernelMessageLogService.getPage(dto));
    }
}
