package com.netty100.pojo.vo;

import com.netty100.entity.AppConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "应用协议配置视图对象")
public class AppConfigVo extends AppConfig {

    @ApiModelProperty(value = "消息源描述")
    private String messageSourceDesc;

    @ApiModelProperty(value = "消息目的地描述")
    private String messageDestDesc;

    @ApiModelProperty(value = "消息序列化方式描述")
    private String messageSerializeDesc;
}
