package com.netty100.controller.admin;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.AccessLog;
import com.netty100.pojo.dto.AccessLogPageQueryDto;
import com.netty100.pojo.vo.AccessLogPvLineChartVo;
import com.netty100.pojo.vo.AccessLogUvLineChartVo;
import com.netty100.service.AccessLogService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author why
 */
@RestController
@RequestMapping("/admin/access-log")
@Api(tags = "【页面功能-管理端-访问历史】")
public class AccessLogController {

    private AccessLogService accessLogService;

    @Autowired
    public void setAccessLogService(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @ApiOperation(value = "pv折线图")
    @GetMapping(value = "/pv/line-chart")
    public WebResult<List<AccessLogPvLineChartVo>> pvLine() {
        List<AccessLogPvLineChartVo> list = accessLogService.getPvLineChartData();
        return WebResult.ok(list);
    }

    @ApiOperation(value = "uv折线图")
    @GetMapping(value = "/uv/line-chart")
    public WebResult<List<AccessLogUvLineChartVo>> uvLine() {
        List<AccessLogUvLineChartVo> list = accessLogService.getUvLineChartData();
        return WebResult.ok(list);
    }

    @ApiOperation(value = "分页获取访问历史记录")
    @PostMapping(value = "/page")
    public WebResult<PageInfo<AccessLog>> page(@RequestBody AccessLogPageQueryDto dto) {
        PageInfo<AccessLog> page = accessLogService.getPage(dto);
        return WebResult.ok(page);
    }
}
