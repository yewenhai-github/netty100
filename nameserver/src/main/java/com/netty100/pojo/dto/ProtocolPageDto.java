package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "消息协议分页查询对象")
public class ProtocolPageDto {

    @ApiModelProperty(value = "第几页,默认为第一页", dataType = "int")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小,默认为10条", dataType = "int")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "排序列,默认为空,不排序", dataType = "string")
    private String orderBy = "protocol_code_value";

    @ApiModelProperty(value = "排序方式,asc或者desc,默认为asc", dataType = "string")
    private String orderFlag = "asc";

    @NotBlank(message = "协议类型不能为空")
    @ApiModelProperty(value = "消息协议类型,message_way,message_type,message_source,message_dest,message_serialize", dataType = "string")
    private String protocolType;
}
