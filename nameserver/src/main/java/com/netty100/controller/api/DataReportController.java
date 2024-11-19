package com.netty100.controller.api;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.netty100.entity.ClientHeartbeatLog;
import com.netty100.entity.ClientChannelLog;

import com.netty100.entity.KernelMessageLog;
import com.netty100.entity.Message;
import com.netty100.entity.ServerHeartBeatLog;
import com.netty100.pojo.dto.*;
import com.netty100.service.*;
import com.netty100.service.ClientChannelLogService;
import com.netty100.service.DataReportService;
import com.netty100.service.KernelMessageLogService;
import com.netty100.service.MessageService;
import com.netty100.service.ServerService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 连接历史相关操作
 * </p>
 *
 * @author why
 * @since 2022-03-25
 */
@Validated
@RestController
@RequestMapping(value = "/api")
@Api(tags = "【sdk-api-数据采集】")
public class DataReportController {

    private DataReportService dataReportService;

    private ServerService serverService;

    private MessageService messageService;

    private ClientHeartbeatLogService clientHeartbeatLogService;

    private ServerHeartbeatLogService serverHeartbeatLogService;

    private ClientChannelLogService clientChannelLogService;

    private KernelMessageLogService kernelMessageLogService;

    @Autowired
    public void setClientHeartbeatLogService(ClientHeartbeatLogService clientHeartbeatLogService) {
        this.clientHeartbeatLogService = clientHeartbeatLogService;
    }

    @Autowired
    public void setServerHeartbeatLogService(ServerHeartbeatLogService serverHeartbeatLogService) {
        this.serverHeartbeatLogService = serverHeartbeatLogService;
    }

    @Autowired
    public void setClientChannelLogService(ClientChannelLogService clientChannelLogService) {
        this.clientChannelLogService = clientChannelLogService;
    }

    @Autowired
    public void setKernelMessageLogService(KernelMessageLogService kernelMessageLogService) {
        this.kernelMessageLogService = kernelMessageLogService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setDataReportService(DataReportService dataReportService) {
        this.dataReportService = dataReportService;
    }

    @Autowired
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }


    @SentinelResource(value = "/api/conn/client/add")
    @ApiOperation(value = "游戏端连接", notes = "游戏端新建连接信息上报")
    @PostMapping(value = "/conn/client/add")
    public WebResult<?> clientConnect(@Valid @RequestBody ClientConnectDto dto) {
        dataReportService.clientConnect(dto);
        return WebResult.ok();
    }


    @SentinelResource(value = "/api/conn/client/delete")
    @ApiOperation(value = "游戏端正常断开", notes = "游戏端连接信息上报")
    @PostMapping(value = "/conn/client/delete")
    public WebResult<?> clientDisconnect(@Valid @RequestBody ClientDisconnectDto dto) {
        dataReportService.clientDisconnect(dto);
        return WebResult.ok();
    }


    @SentinelResource(value = "/api/conn/client/error")
    @ApiOperation(value = "游戏端异常断开", notes = "游戏端连接异常断开信息上报")
    @PostMapping(value = "/conn/client/error")
    public WebResult<?> clientAbnormalDisconnect(@Valid @RequestBody ClientDisconnectDto dto) {
        dataReportService.clientErrorDisconnect(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/conn/client/heartbeat")
    @ApiOperation(value = "游戏端心跳断开", notes = "游戏端连接心跳断开信息上报")
    @PostMapping(value = "/conn/client/heartbeat")
    public WebResult<?> clientHeartbeatDisconnect(@Valid @RequestBody ClientDisconnectDto dto) {
        dataReportService.clientHeartBeatDisconnect(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/conn/server/add")
    @ApiOperation(value = "服务器连接", notes = "服务器端新建连接信息上报")
    @PostMapping(value = "/conn/server/add")
    public WebResult<?> serverConnect(@Valid @RequestBody ServerConnectDto dto) {
        dataReportService.serverConnect(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/conn/server/delete")
    @ApiOperation(value = "服务器正常断开", notes = "服务器端正常断开连接信息上报")
    @PostMapping(value = "/conn/server/delete")
    public WebResult<?> serverDisconnect(@Valid @RequestBody ServerDisconnectDto dto) {
        dataReportService.serverDisconnect(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/conn/server/error")
    @ApiOperation(value = "服务器异常断开", notes = "服务器端连接异常断开信息上报")
    @PostMapping(value = "/conn/server/error")
    public WebResult<?> serverErrorDisconnect(@Valid @RequestBody ServerDisconnectDto dto) {
        dataReportService.serverErrorDisconnect(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/conn/server/heartbeat")
    @ApiOperation(value = "服务器心跳断开", notes = "服务器端连接心跳断开信息上报")
    @PostMapping(value = "/conn/server/heartbeat")
    public WebResult<?> serverHeartbeatDisconnect(@Valid @RequestBody ServerDisconnectDto dto) {
        dataReportService.serverHeartbeatDisconnect(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/node/boot")
    @ApiOperation(value = "节点启动", notes = "netty节点启动上报")
    @PostMapping(value = "/node/boot")
    public WebResult<?> bootReport(@RequestBody @Valid BootReportDto dto) {
        serverService.bootReport(dto);
        return WebResult.ok();
    }


    @SentinelResource(value = "/api/node/heartbeat")
    @ApiOperation(value = "节点心跳", notes = "节点心跳信息上报,包含服务器连接数信息和游戏端连接数信息")
    @PostMapping(value = "/node/heartbeat")
    public WebResult<?> reportConnectionInfo(@RequestBody ReportConnectionCountDto dto) {
        serverService.reportConnectionCount(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/node/report")
    @ApiOperation(value = "节点流量上报", notes = "流量,连接相关汇总信息")
    @PostMapping(value = "/node/report")
    public WebResult<?> reportMessage(@Valid @RequestBody ReportMessageDto dto) {
        serverService.reportMessage(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/message/report")
    @ApiOperation(value = "消息上报", notes = "游戏端消息随机上报")
    @PostMapping(value = "/message/report")
    public WebResult<?> message(@RequestBody List<Message> messages) {
        messageService.batchInsert(messages);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/node/shutdown")
    @ApiOperation(value = "节点停机上报")
    @PostMapping(value = "/node/shutdown")
    public WebResult<?> shutdown(@Validated @RequestBody ServerShutdownDto dto) {
        dataReportService.shutdown(dto);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/client/heartbeat/log/report")
    @ApiOperation(value = "游戏端心跳日志上报")
    @PostMapping(value = "/client/heartbeat/log/report")
    public WebResult<Boolean> clientHeartbeatLogReport(@RequestBody List<ClientHeartbeatLog> data) {
        clientHeartbeatLogService.processData(data);
        return WebResult.ok(true);
    }

    @SentinelResource(value = "/api/server/heartbeat/log/report")
    @ApiOperation(value = "服务器端心跳日志上报")
    @PostMapping(value = "/server/heartbeat/log/report")
    public WebResult<Boolean> serverHeartbeatLogReport(@RequestBody List<ServerHeartBeatLog> data) {
        serverHeartbeatLogService.processData(data);
        return WebResult.ok(true);
    }

    @SentinelResource(value = "/api/client/channel/report")
    @ApiOperation(value = "连接日志上报")
    @PostMapping(value = "/client/channel/report")
    public WebResult<?> clientChannelLogReport(@RequestBody List<ClientChannelLog> data) {
        clientChannelLogService.batchSave(data);
        return WebResult.ok();
    }

    @SentinelResource(value = "/api/kernel/log/report")
    @ApiOperation(value = "内核日志上报")
    @PostMapping(value = "/kernel/log/report")
    public WebResult<Boolean> kernelMessageLogReport(@RequestBody List<KernelMessageLog> data) {
        kernelMessageLogService.batchSave(data);
        return WebResult.ok(true);
    }
}
