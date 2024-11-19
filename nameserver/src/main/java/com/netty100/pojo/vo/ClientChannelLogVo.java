package com.netty100.pojo.vo;

import com.netty100.entity.ClientChannelLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "连接日志视图对象")
public class ClientChannelLogVo extends ClientChannelLog {

    @ApiModelProperty(value = "消息源描述")
    private String messageSourceDesc;

    @ApiModelProperty(value = "消息目的地描述")
    private String messageDestDesc;
}
