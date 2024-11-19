package com.netty100.pojo.vo;

import com.netty100.entity.ReportWarnStatisticsRate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "统计页面数据模型")
public class StatisticsVo {

    @ApiModelProperty(value = "告警类型饼图数据")
    private List<WarnTypeCount> warnTypeCountList;

    @ApiModelProperty(value = "告警增长率")
    private List<WarnIncreaseRate> warnIncreaseRates;

    @ApiModelProperty(value = "流量")
    private ArrayList<FlowVo> flowVos;

    @ApiModelProperty(value = "c2p流量")
    private BigDecimal c2pFlow;

    @ApiModelProperty(value = "p2s流量")
    private BigDecimal p2sFlow;

    @ApiModelProperty(value = "s2p流量")
    private BigDecimal s2pFlow;

    @ApiModelProperty(value = "p2c流量")
    private BigDecimal p2cFlow;

    @ApiModelProperty(value = "转发流量 ")
    private BigDecimal relayFlow;

    @ApiModelProperty(value = "tps-qps统计数据 ")
    private List<TpsQpsMinuteTotalVo> tpsQps;

    @ApiModelProperty(value = "消息相关数据")
    private MessageQuality messageQuality;

    @ApiModelProperty(value = "历史登录设备数量")
    private List<UserLocationVo> historicalUserLocation;

    @ApiModelProperty(value = "活跃设备数量")
    private List<UserLocationVo> activeUserLocation;
}
