package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "项目协议配置")
public class AppConfig implements Serializable {

    @ApiModelProperty(value = "主键", hidden = true)
    private Long id;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "集群名称")
    private String cluster;

    @ApiModelProperty(value = "消息源")
    private Byte messageSource;

    @ApiModelProperty(value = "消息目的地")
    private Byte messageDest;

    @ApiModelProperty(value = "消息序列化方式")
    private Byte messageSerialize;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
