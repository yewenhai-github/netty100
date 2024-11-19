package com.netty100.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author why
 */
@Getter
@Setter
@ApiModel(value = "用户集群关联对象")
public class UserClusterVo {

    @ApiModelProperty(value = "用户集群关联,集群键")
    private ClusterVo2 clusterVo2;

    @ApiModelProperty(value = "用户集群关联,用户值")
    private List<UserVo> userVoList;

    @Getter
    @Setter
    @ApiModel(value = "用户集群关联,集群键")
    public static class ClusterVo2 {

        private Integer clusterId;

        private String cluster;

        @JsonFormat(timezone = "GMT+8:00", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createTime;

    }

    @Getter
    @Setter
    @ApiModel(value = "用户集群关联,用户值")
    public static class UserVo {

        private Integer userId;

        private String username;

    }
}
