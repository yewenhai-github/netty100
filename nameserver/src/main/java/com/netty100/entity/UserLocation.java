package com.netty100.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户分布,一个userId只能在
 * 一个市区内出现一次
 *
 * @author why
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "游戏端用户地区分布")
public class UserLocation {

    @ApiModelProperty(value = "主键", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "省份", dataType = "string")
    private String province;

    @ApiModelProperty(value = "市区", dataType = "string")
    private String city;

    @ApiModelProperty(value = "游戏端用户主键", dataType = "long")
    private Long userId;

}
