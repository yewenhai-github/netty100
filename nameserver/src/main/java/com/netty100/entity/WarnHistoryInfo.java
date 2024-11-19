package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Getter
@Setter
@ApiModel(value = "告警记录")
public class WarnHistoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", dataType = "int")
    private Integer id;

    @ApiModelProperty(value = "集群id", dataType = "int")
    private Integer clusterId;

    @ApiModelProperty(value = "告警来源,serverId", dataType = "int")
    private Integer serverId;

    @ApiModelProperty(value = "告警等级", dataType = "int")
    private Integer warnLevel;

    @ApiModelProperty(value = "告警类型", dataType = "int")
    private Integer warnType;

    @ApiModelProperty(value = "告警详情", dataType = "string")
    private String detail;

    @ApiModelProperty(value = "创建时间", dataType = "string")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "告警生产所在天", dataType = "string")
    private String createDay;

    @ApiModelProperty(value = "告警类型简称",dataType = "string")
    private String shortName;

    @JsonIgnore
    @ApiModelProperty(value = "分析标记", dataType = "int")
    private Integer statisticsFlag;
}
