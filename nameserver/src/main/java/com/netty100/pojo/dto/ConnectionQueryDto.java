package com.netty100.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DateUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "连接查询参数相关")
public class ConnectionQueryDto {

    @ApiModelProperty(value = "游戏端应用中的userId", dataType = "long")
    private Long userId;

    @ApiModelProperty(value = "连接地址", required = true, dataType = "string")
    private String remoteIp;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询开始时间", required = true, dataType = "string")
    private Date start;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "查询结束时间", required = true, dataType = "string")
    private Date end;

    @ApiModelProperty(value = "消息方式", dataType = "byte")
    private Byte messageWay;

    @ApiModelProperty(value = "消息源", dataType = "byte")
    private Byte messageSource;

    @ApiModelProperty(value = "消息目的地", dataType = "byte")
    private Byte messageDest;

    @ApiModelProperty(value = "消息类型", dataType = "byte")
    private Byte messageType;

    @ApiModelProperty(value = "序列化方式", dataType = "byte")
    private Byte messageSerialize;

    @ApiModelProperty(value = "第几页,默认为1", dataType = "int")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认为10", dataType = "int")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为连接时间", dataType = "string")
    private String orderBy = "connect_time";

    @ApiModelProperty(value = "排序方式,可选值为asc,desc,默认为desc", dataType = "string")
    private String orderFlag = "desc";

    @ApiModelProperty(value = "节点主键", required = true, dataType = "int")
    private Integer serverId;
}
