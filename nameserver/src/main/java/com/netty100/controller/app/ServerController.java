package com.netty100.controller.app;

import com.github.pagehelper.PageInfo;
import com.netty100.entity.Cluster;
import com.netty100.pojo.dto.ConnectionQueryDto;
import com.netty100.pojo.dto.ServerListDto;
import com.netty100.pojo.vo.ClientConnectionVo;
import com.netty100.pojo.vo.ServerBriefVo;
import com.netty100.pojo.vo.ServerConnectionVo;
import com.netty100.pojo.vo.ServerListVo;
import com.netty100.service.ClientConnectionService;
import com.netty100.service.ClusterService;
import com.netty100.service.ServerConnectionService;
import com.netty100.service.ServerService;
import com.netty100.utils.WebResult;
import com.netty100.utils.exception.CommonException;
import com.netty100.utils.respons.ResponseMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @author why
 */
@Slf4j
@Validated
@RestController(value = "app-server")
@RequestMapping("/app")
@RequiredArgsConstructor
@Api(tags = "【页面功能-用户端-节点】")
public class ServerController {

    private ClusterService clusterService;

    private ServerService serverService;

    private ClientConnectionService clientConnectionService;

    private ServerConnectionService serverConnectionService;

    @ApiOperation(value = "游戏端连接查询")
    @PostMapping(value = "/node/conn/client/page")
    public WebResult<PageInfo<ClientConnectionVo>> getClientPage(@Valid @RequestBody ConnectionQueryDto dto) {
        if (StringUtils.hasText(dto.getRemoteIp())) {
            String ip = dto.getRemoteIp();
            ip = "%" + ip.trim() + "%";
            dto.setRemoteIp(ip);
        }
        return WebResult.ok(clientConnectionService.getPage(dto));
    }

    @ApiOperation(value = "服务器连接查询")
    @PostMapping(value = "/node/conn/server/page")
    public WebResult<PageInfo<ServerConnectionVo>> getServerPage(@Valid @RequestBody ConnectionQueryDto dto) {
        if (StringUtils.hasText(dto.getRemoteIp())) {
            String ip = dto.getRemoteIp();
            ip = "%" + ip.trim() + "%";
            dto.setRemoteIp(ip);
        }
        return WebResult.ok(serverConnectionService.getPage(dto));
    }

    @ApiOperation(value = "节点分页查询")
    @PostMapping(value = "/node/page")
    public WebResult<PageInfo<ServerListVo>> serverList(@Valid @RequestBody ServerListDto dto) {
        final Cluster cluster = clusterService.getById(dto.getClusterId());
        if (Objects.isNull(cluster)) {
            throw new CommonException(ResponseMsg.CLUSTER_NOT_EXIST);
        }
        return WebResult.ok(serverService.getServerPage(dto));
    }

    @ApiOperation(value = "节点列表", notes = "获取指定集群下节点简略信息")
    @PostMapping(value = "/node/list/{clusterId}")
    public WebResult<List<ServerBriefVo>> briefServerList(@PathVariable(value = "clusterId") Integer clusterId) {
        final Cluster cluster = clusterService.getById(clusterId);
        if (Objects.isNull(cluster)) {
            throw new CommonException(ResponseMsg.CLUSTER_NOT_EXIST);
        }
        return WebResult.ok(serverService.briefServerList(clusterId));
    }

    @Autowired
    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Autowired
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setClientConnectionService(ClientConnectionService clientConnectionService) {
        this.clientConnectionService = clientConnectionService;
    }

    @Autowired
    public void setServerConnectionService(ServerConnectionService serverConnectionService) {
        this.serverConnectionService = serverConnectionService;
    }
}

