package com.netty100.controller.app;


import com.netty100.pojo.dto.StatisticsQueryDto;
import com.netty100.pojo.vo.StatisticsVo;
import com.netty100.service.StatisticalAnalysisService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 统计分析
 * 如果没有选择集群,默认展示数据为登录账户负责的所有集群
 * 管理员为所有集群
 * 传递clusterId = 0
 *
 * @author why
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/app")
@Api(tags = "【页面功能-用户端-统计】")
public class StatisticalAnalysisController {

    private StatisticalAnalysisService statisticalAnalysisService;

    @Autowired
    public void setStatisticalAnalysisService(StatisticalAnalysisService statisticalAnalysisService) {
        this.statisticalAnalysisService = statisticalAnalysisService;
    }

    @ApiOperation(value = "数据获取", notes = "统计分析页面展示数据获取")
    @PostMapping(value = "/statistics")
    public WebResult<StatisticsVo> data(@Validated @RequestBody StatisticsQueryDto dto) {

        return WebResult.ok(statisticalAnalysisService.getStatisticsData(dto));
    }
}
