package com.netty100.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "访问pv折线图数据点")
public class AccessLogPvLineChartVo {

    @ApiModelProperty(value = "访问日期",dataType = "string")
    private String createDate;

    @ApiModelProperty(value = "访问亮",dataType = "long")
    private Long visitTimes;
}
