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
@ApiModel(value = "用户集群关联对象", description = "")
public class UserCluster implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "用户主键")
    private Integer userId;

    @ApiModelProperty(value = "集群主键")
    private Integer clusterId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
}
