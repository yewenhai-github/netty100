package com.netty100.controller.app;

import com.netty100.entity.Cluster;
import com.netty100.pojo.vo.ClusterVo;
import com.netty100.service.ClusterService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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
@RestController(value = "app-cluster")
@RequestMapping("/app/cluster")
@RequiredArgsConstructor
@Api(tags = "【页面功能-用户端-集群】")
public class ClusterController {

    private final ClusterService clusterService;

    /**
     * 对应原型集群页面
     */
    @ApiOperation(value = "获取列表", notes = "获取负责集群的详细信息")
    @PostMapping(value = "/list")
    public WebResult<List<ClusterVo>> getClusters() {
        List<ClusterVo> clusters = clusterService.getClusters();
        if (Objects.isNull(clusters)) {
            return WebResult.ok(Collections.emptyList());
        }
        return WebResult.ok(clusters);
    }

    /**
     * 获取当前用户负责的集群列表,只包含从app_cluster表中查询出来的信息
     */
    @ApiOperation(value = "简略信息", notes = "获取负责集群的简单信息")
    @PostMapping(value = "/brief/list")
    public WebResult<List<Cluster>> getClusterInfo() {
        final List<Cluster> clusters = clusterService.getClusterInfo();
        if (Objects.isNull(clusters)) {
            return WebResult.ok(Collections.emptyList());
        }
        return WebResult.ok(clusters);
    }
}
