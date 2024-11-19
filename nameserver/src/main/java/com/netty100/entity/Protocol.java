package com.netty100.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
@ApiModel(value = "MessageProtocol对象", description = "消息协议对象")
public class Protocol implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", dataType = "int")
    private Integer id;

    @ApiModelProperty(value = "消息协议类型,message_way,message_type,message_source,message_dest,message_serialize", dataType = "string")
    private String protocolType;

    @ApiModelProperty(value = "消息协议代码", dataType = "string")
    private String protocolCode;

    @ApiModelProperty(value = "消息协议名称", dataType = "string")
    private String protocolName;

    @ApiModelProperty(value = "描述信息", dataType = "string")
    private String protocolDesc;
}
