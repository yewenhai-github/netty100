package com.netty100.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value = "集群")
public class Cluster implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "集群主键")
    private Integer id;

    @ApiModelProperty(value = "集群名称")
    private String cluster;

    @ApiModelProperty(value = "集群描述信息")
    private String description;

    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
