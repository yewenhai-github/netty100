package com.netty100.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "疑难杂症分页查询对象")
public class IssuePageQueryDto {

    @ApiModelProperty(value = "问题类型")
    private String issueType;

    @ApiModelProperty(value = "标题标题")
    private String title;

    @ApiModelProperty(value = "重要性")
    private Integer importance;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String orderBy = "importance";

    private String orderFlag = "desc";
}
