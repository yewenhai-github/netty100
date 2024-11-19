package com.netty100.pojo.dto;

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
@ApiModel(value = "设备注册码查询对象")
public class RegistrationDetailQueryDto {

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询开始时间")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询结束时间")
    private Date endTime;

    @ApiModelProperty(value = "设备id")
    private Long deviceId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "第几页,默认为1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认为10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为创建时间")
    private String orderBy = "create_time";

    @ApiModelProperty(value = "排序方式,默认为降序")
    private String orderFlag = "desc";
}
