package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "内核节点日志")
public class KernelMessageLog {

    @ApiModelProperty(value = "主键",hidden = true)
    private Long id;

    @ApiModelProperty(value = "日志点")
    private String logPoint;

    @ApiModelProperty(value = "日志内容")
    private String logContent;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "输出日志时间")
    private Date logTime;

    @ApiModelProperty(value = "内核本地地址")
    private String localAddress;

    @ApiModelProperty(value = "用户标识")
    private Long userId;

    @ApiModelProperty(value = "设备标识")
    private Long deviceId;

    @ApiModelProperty(value = "消息源")
    private Byte messageSource;

    @ApiModelProperty(value = "消息目的地")
    private Byte messageDest;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;
}
