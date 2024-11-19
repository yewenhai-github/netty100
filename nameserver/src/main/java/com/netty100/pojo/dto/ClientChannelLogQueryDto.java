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
@Getter
@Setter
@ApiModel(value = "连接日志查询对象")
public class ClientChannelLogQueryDto {

    @ApiModelProperty(value = "查询开始时间")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "查询结束时间")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "远程连接地址")
    private String remoteAddress;

    @ApiModelProperty(value = "netty节点本地地址")
    private String localAddress;

    @ApiModelProperty(value = "消息源")
    private Integer messageSource;

    @ApiModelProperty(value = "消息目的地")
    private Integer messageDest;

    @ApiModelProperty(value = "用户标识")
    private Long userId;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String orderBy = "log_time";

    private String orderFlag = "desc";
}
