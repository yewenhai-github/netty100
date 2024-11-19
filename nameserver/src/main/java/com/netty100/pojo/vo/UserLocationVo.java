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
@ApiModel(value = "用户分布")
public class UserLocationVo {

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "统计设备数量")
    private Integer total;
}
