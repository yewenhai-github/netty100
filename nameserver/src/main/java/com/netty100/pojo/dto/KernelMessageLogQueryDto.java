package com.netty100.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * @author why
 */
@ApiModel(value = "内核日志查询对象")
@Getter
@Setter
public class KernelMessageLogQueryDto {

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询开始时间")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询结束时间")
    private Date endTime;

    @ApiModelProperty(value = "用户标识")
    private Long userId;

    @ApiModelProperty(value = "设备标识")
    private Long deviceId;

    @ApiModelProperty(value = "日志点")
    private String logPoint;

    @ApiModelProperty(value = "netty节点本地地址")
    private String localAddress;

    @ApiModelProperty(value = "第几页,默认第一页")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为空")
    private String orderBy = "log_time";

    @ApiModelProperty(value = "排序方式,默认为降序")
    private String orderFlag = "desc";


}
