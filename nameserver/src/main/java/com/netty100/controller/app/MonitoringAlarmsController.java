package com.netty100.controller.app;

import com.netty100.enumeration.WarnType;
import com.netty100.pojo.dto.MonitorDto;
import com.netty100.pojo.vo.MonitorResultVo;
import com.netty100.service.MonitoringAlarmsService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 监控告警页面
 *
 * @author why
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/app")
@Api(tags = "【页面功能-用户端-监控】")
public class MonitoringAlarmsController {

    private MonitoringAlarmsService monitoringAlarmsService;

    @Autowired
    public void setMonitoringAlarmsService(MonitoringAlarmsService monitoringAlarmsService) {
        this.monitoringAlarmsService = monitoringAlarmsService;
    }

    @ApiOperation(value = "数据获取", notes = "监控页面展示数据获取")
    @PostMapping(value = "/monitoring")
    public WebResult<MonitorResultVo> data(@Validated @RequestBody MonitorDto dto) {
        return WebResult.ok(monitoringAlarmsService.getMonitorData(dto));
    }

    @ApiOperation(value = "告警类型名称")
    @PostMapping(value = "/warn/types")
    public WebResult<List<Warn>> getList() {
        val list = new ArrayList<Warn>();
        Arrays.stream(WarnType.values()).forEach(x -> {
            val warn = new Warn();
            warn.setIndex(x.getIndex());
            warn.setName(x.getTitle());
            list.add(warn);
        });
        return WebResult.ok(list);
    }

    @Getter
    @Setter
    @ApiModel(value = "告警")
    public static class Warn {

        @ApiModelProperty(value = "序号")
        private Integer index;

        @ApiModelProperty(value = "告警类型")
        private String name;
    }

}
