package com.netty100.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "问题")
public class Issue {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "问题类型")
    private String issueType;

    @ApiModelProperty(value = "标题标题")
    private String title;

    @ApiModelProperty(value = "问题描述及解决方案")
    private String content;

    @ApiModelProperty(value = "重要性,高->3,中->2,低->1")
    private Integer importance;
}
