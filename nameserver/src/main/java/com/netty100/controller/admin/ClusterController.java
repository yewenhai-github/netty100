package com.netty100.controller.admin;


import com.netty100.entity.Cluster;
import com.netty100.pojo.dto.AddClusterDto;
import com.netty100.pojo.dto.AssignClusterToUserDto;
import com.netty100.pojo.dto.UpdateClusterDto;
import com.netty100.pojo.vo.UserClusterVo;
import com.netty100.service.ClusterService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
@Validated
@RestController(value = "admin-cluster")
@RequestMapping("/admin/cluster")
@RequiredArgsConstructor
@Api(tags = "【页面功能-管理端-集群管理】")
public class ClusterController {

    private ClusterService clusterService;

    @ApiOperation(value = "添加", notes = "管理员添加集群")
    @PostMapping(value = "/add")
    public WebResult<?> add(@Validated @RequestBody AddClusterDto dto) {
        clusterService.addCluster(dto);
        return WebResult.ok();
    }

    /**
     * 更新集群信息
     */
    @ApiOperation(value = "更新", notes = "管理员更新集群信息")
    @PostMapping(value = "/update")
    public WebResult<?> update(@Validated @RequestBody UpdateClusterDto dto) {
        clusterService.updateCluster(dto);
        return WebResult.ok();
    }

    /**
     * 用户集群关联,为集群分配负责人
     */
    @ApiOperation(value = "用户关联", notes = "管理员为集群分配负责人")
    @PostMapping(value = "/assign")
    public WebResult<?> assign(@Validated @RequestBody AssignClusterToUserDto dto) {
        clusterService.assign(dto);
        return WebResult.ok();
    }

    @ApiOperation(value = "简略信息", notes = "获取管理集群简略信息")
    @PostMapping(value = "/info")
    public WebResult<List<Cluster>> getClusterInfo() {
        final List<Cluster> clusterInfo = clusterService.getClusterInfo();
        if(Objects.isNull(clusterInfo)){
            return WebResult.ok(Collections.emptyList());
        }
        return WebResult.ok(clusterInfo);
    }

    @ApiOperation(value = "用户集群关联列表")
    @PostMapping(value = "/user-cluster/list")
    public WebResult<List<UserClusterVo>> getUserClusters() {
        return WebResult.ok(clusterService.getUserClusters());
    }

    @Autowired
    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }
}
