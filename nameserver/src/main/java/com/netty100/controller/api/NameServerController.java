package com.netty100.controller.api;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.netty100.entity.AppConfig;
import com.netty100.entity.Cluster;
import com.netty100.entity.Protocol;
import com.netty100.pojo.dto.ClientServerQueryDto;
import com.netty100.pojo.dto.NodeListClusterDto;
import com.netty100.pojo.dto.NodeListParamDto;
import com.netty100.pojo.dto.RegistrationCodeDto;
import com.netty100.pojo.vo.ServerVo;
import com.netty100.pojo.vo.ServerVoV2;
import com.netty100.service.RegistrationDetailService;
import com.netty100.utils.RedisTool;
import com.netty100.utils.RoutingTool;
import com.netty100.utils.WebResult;
import com.netty100.utils.exception.CommonException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author why
 */
@Slf4j
@SuppressWarnings({"DuplicatedCode"})
@Api(tags = "【sdk-api-nameserver】")
@RestController
@RequestMapping(value = "/api/nameserver")
public class NameServerController {

    private RegistrationDetailService registrationDetailService;

    private RedisTool redisTool;

    private final Map<String, List<ServerVo>> clientMap = new ConcurrentHashMap<>();
    private final Map<String, List<ServerVo>> serverMap = new ConcurrentHashMap<>();
    private final Map<String, List<ServerVo>> nodeMap = new ConcurrentHashMap<>();
    private volatile Map<String, List<Protocol>> protocols;
    private volatile List<AppConfig> appConfigs;

    @Autowired
    public void setRegistrationDetailService(RegistrationDetailService registrationDetailService) {
        this.registrationDetailService = registrationDetailService;
    }

    @Autowired
    public void setRedisTool(RedisTool redisTool) {
        this.redisTool = redisTool;
    }

    @SentinelResource(value = "/api/nameserver/client/list")
    @ApiOperation(value = "游戏端节点获取", notes = "游戏端获取节点信息列表,以便同netty节点建立tcp长连接")
    @PostMapping(value = "/client/list")
    public WebResult<List<ServerVo>> getServers0(@RequestBody ClientServerQueryDto dto) {
        if (StringUtils.isEmpty(dto.getCluster())) {
            return (WebResult<List<ServerVo>>) WebResult.error("参数错误");
        }
        try {
            val clusters = redisTool.clusters();
            Optional<Cluster> optional = clusters.stream().filter(x -> Objects.equals(x.getCluster(), dto.getCluster().trim())).findFirst();
            if (optional.isPresent()) {
                val servers = redisTool.servers().stream().filter(x -> Objects.equals(x.getClusterId(), optional.get().getId())).collect(Collectors.toList());
                RoutingTool.process(servers);
                for (ServerVo x : servers) {
                    x.setIntranetIp(null);
                    x.setDomain(null);
                }
                clientMap.put(dto.getCluster(), servers);
                return WebResult.ok(servers);
            } else {
                return WebResult.ok(Collections.emptyList());
            }
        } catch (Exception e) {
            log.error("游戏端获取节点信息列表,redis执行失败", e);
            return WebResult.ok(clientMap.getOrDefault(dto.getCluster(), Collections.emptyList()));
        }
    }

    @SentinelResource(value = "/api/nameserver/client/list/v2")
    @ApiOperation(value = "游戏端节点获取", notes = "游戏端获取节点信息列表,以便同netty节点建立tcp长连接")
    @PostMapping(value = "/client/list/v2")
    public WebResult<List<ServerVoV2>> getServers0V2(@RequestBody ClientServerQueryDto dto) {
        if (StringUtils.isEmpty(dto.getCluster())) {
            return (WebResult<List<ServerVoV2>>) WebResult.error("参数错误");
        }
        List<ServerVo> serverVoList = null;
        try {
            val clusters = redisTool.clusters();
            Optional<Cluster> optional = clusters.stream().filter(x -> Objects.equals(x.getCluster(), dto.getCluster().trim())).findFirst();
            if (!optional.isPresent()) {
                return WebResult.ok(Collections.emptyList());
            }

            val servers = redisTool.servers().stream().filter(x -> Objects.equals(x.getClusterId(), optional.get().getId())).collect(Collectors.toList());
            RoutingTool.process(servers);
            serverVoList = servers;
        } catch (Exception e) {
            log.error("游戏端获取节点信息列表,redis执行失败", e);
            serverVoList = clientMap.getOrDefault(dto.getCluster(), Collections.emptyList());
        }
        List<ServerVoV2> collect = serverVoList.stream().map(ServerVoV2::new).collect(Collectors.toList());
        return WebResult.ok(collect);
    }

    @SentinelResource(value = "/api/nameserver/server/list")
    @ApiOperation(value = "服务器节点获取", notes = "游戏端获取节点信息列表,以便同netty节点建立tcp长连接")
    @PostMapping(value = "/server/list")
    public WebResult<List<ServerVo>> getServers1(@RequestBody NodeListClusterDto dto) {
        try {
            val clusters = redisTool.clusters();
            Optional<Cluster> optional = clusters.stream().filter(x -> Objects.equals(x.getCluster(), dto.getCluster().trim())).findFirst();
            if (optional.isPresent()) {
                val servers = redisTool.servers().stream().filter(x -> Objects.equals(x.getClusterId(), optional.get().getId())).collect(Collectors.toList());
                serverMap.put(dto.getCluster(), servers);
                return WebResult.ok(servers);
            } else {
                return WebResult.ok(Collections.emptyList());
            }
        } catch (Exception e) {
            log.error("服务器端获取节点信息列表,redis执行失败", e);
            return WebResult.ok(serverMap.getOrDefault(dto.getCluster(), Collections.emptyList()));
        }
    }

    @SentinelResource(value = "/api/nameserver/node/list")
    @ApiOperation(value = "平台获取节点", notes = "netty节点获取所属集群下的其他节点信息,以便建立连接")
    @PostMapping(value = "/node/list")
    public WebResult<List<ServerVo>> getServers2(@RequestBody NodeListParamDto dto) {
        val key = JSON.toJSONString(dto);
        try {
            Optional<ServerVo> optional = redisTool.servers().stream().filter(x -> Objects.equals(x.getIntranetIp(), dto.getIntranetIp().trim()) && Objects.equals(x.getPort(), dto.getPort().trim())).findFirst();
            if (optional.isPresent()) {
                val servers = redisTool.servers().stream().filter(x -> Objects.equals(x.getClusterId(), optional.get().getClusterId())).collect(Collectors.toList());
                nodeMap.put(key, servers);
                return WebResult.ok(servers);
            } else {
                return WebResult.ok(Collections.emptyList());
            }
        } catch (Exception e) {
            log.error("netty节点获取节点信息列表,redis执行失败", e);
            return WebResult.ok(nodeMap.getOrDefault(key, Collections.emptyList()));
        }
    }

    @SentinelResource(value = "/api/nameserver/protocol/list")
    @ApiOperation(value = "消息协议列表", notes = "获取平台支持的消息协议列表", hidden = true)
    @PostMapping(value = "/protocol/list")
    public WebResult<Map<String, List<Protocol>>> getAll() {
        try {
            protocols = redisTool.protocols();
        } catch (Exception e) {
            log.error("获取消息协议列表,redis执行失败", e);
        }
        return WebResult.ok(protocols);
    }

    @SentinelResource(value = "/api/nameserver/app-config/list")
    @ApiOperation(value = "netty平台获取应用协议配置")
    @PostMapping(value = "/app-config/list")
    public WebResult<List<AppConfig>> appConfigListByNode() {
        try {
            appConfigs = redisTool.appConfigs();
        } catch (Exception e) {
            log.error("获取应用协议配置,redis执行失败", e);
        }
        return WebResult.ok(appConfigs);
    }

    @SentinelResource(value = "/api/nameserver/node/registration-code")
    @ApiOperation(value = "netty平台获取设备注册码")
    @PostMapping(value = "/node/registration-code")
    public WebResult<String> getDevicePwdByNode(@Validated @RequestBody RegistrationCodeDto dto) {
        final String devicePwd = registrationDetailService.getDevicePwdByNode(dto.getDeviceId(), dto.getUserId());
        if (Objects.isNull(devicePwd)) {
            throw new CommonException("未找到设备注册码");
        }
        return WebResult.ok(devicePwd);
    }

    @SentinelResource(value = "/api/nameserver/client/registration-code")
    @ApiOperation(value = "客户端获取设备注册码")
    @PostMapping(value = "/client/registration-code")
    public WebResult<String> getDevicePwd(@Validated @RequestBody RegistrationCodeDto dto) {
        return WebResult.ok(registrationDetailService.getDevicePwd(dto.getDeviceId(), dto.getUserId()));
    }
}
