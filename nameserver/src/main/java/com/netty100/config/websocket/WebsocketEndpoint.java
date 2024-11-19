package com.netty100.config.websocket;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.netty100.config.LocalCacheManager;
import com.netty100.entity.AppConfig;
import com.netty100.entity.User;
import com.netty100.pojo.dto.MonitorDto;
import com.netty100.pojo.dto.OnlineDebugDto;
import com.netty100.pojo.dto.StatisticsQueryDto;
import com.netty100.pojo.vo.ServerVo;
import com.netty100.pojo.vo.StatisticsVo;
import com.netty100.service.*;
import com.netty100.utils.WebResult;
import com.netty100.utils.respons.ResponseMsg;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author why
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{userId}")
public class WebsocketEndpoint {

    private static StatisticalAnalysisService statisticalAnalysisService;

    private static MonitoringAlarmsService monitoringAlarmsService;

    private static MessageSender messageSender;

    private static TaskManager taskManager;

    private static ServerService serverService;

    private static UserService userService;

    private static OnlineDebugService onlineDebugService;

    @Autowired
    public void setOnlineDebugService(OnlineDebugService onlineDebugService) {
        WebsocketEndpoint.onlineDebugService = onlineDebugService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        WebsocketEndpoint.userService = userService;
    }

    @Autowired
    public void setServerService(ServerService serverService) {
        WebsocketEndpoint.serverService = serverService;
    }

    @Autowired
    public void setTaskManager(TaskManager taskManager) {
        WebsocketEndpoint.taskManager = taskManager;
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        WebsocketEndpoint.messageSender = messageSender;
    }

    @Autowired
    public void setStatisticalAnalysisService(StatisticalAnalysisService statisticalAnalysisService) {
        WebsocketEndpoint.statisticalAnalysisService = statisticalAnalysisService;
    }

    @Autowired
    public void setMonitoringAlarmsService(MonitoringAlarmsService monitoringAlarmsService) {
        WebsocketEndpoint.monitoringAlarmsService = monitoringAlarmsService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        final User user = userService.getById(userId);
        if (Objects.isNull(user)) {
            messageSender.sendTextMessage(session, "错误的userId,找不到指定的用户", userId);
            try {
                session.close();
            } catch (IOException e) {
                log.error("错误的userId,找不到指定的用户,关闭session失败,userId={}", userId, e);
            }
            return;
        }
        log.info("userId={}连接成功", userId);
        SessionManager.add(userId, session);
    }

    @OnClose
    public void onClose(@PathParam("userId") Integer userId) {
        final User user = userService.getById(userId);
        if (Objects.nonNull(user)) {
            SessionManager.delete(userId);
            taskManager.removeTaskIfPresent(userId);
            log.info("userId={}连接断开", userId);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("userId") Integer userId) {
        final String heartBeatMessage = "1";
        if (!StringUtils.hasText(message) || Objects.equals(message, heartBeatMessage)) {
            messageSender.sendTextMessage(session, "1", userId);
            return;
        }
        log.info("接收到userId={}的消息:{}", userId, message);
        try {
            val onlineDebugDto = JSON.parseObject(message, OnlineDebugDto.class);
            String result = "在线调试功能不支持的消息类型";
            switch (onlineDebugDto.getType()) {
                case 0:
                    result = onlineDebugService.connect(onlineDebugDto);
                    messageSender.sendTextMessage(session, result, userId);
                    return;
                case 1:
                    result = onlineDebugService.onlineDebug(onlineDebugDto);
                    messageSender.sendTextMessage(session, result, userId);
                    return;
                case 2:
                    result = onlineDebugService.disconnect(onlineDebugDto);
                    messageSender.sendTextMessage(session, result, userId);
                    return;
                default:
                    messageSender.sendTextMessage(session, result, userId);
                    return;
            }
        } catch (Exception e) {
            //ignore
        }
        try {
            WebResult<?> result = processMessage(message, userId);
            if (Objects.isNull(result)) {
                result = WebResult.error("不支持的消息类型");
            }
            messageSender.sendTextMessage(session, result, userId);
        } catch (Exception e) {
            log.error("消息处理失败", e);
        }
    }

    @OnError
    public void onError(Throwable error, @PathParam("userId") Integer userId) {
        log.error("userId={}消息处理发生异常", userId, error);
    }

    private WebResult<?> processMessage(String message, Integer userId) {
        //移除之前的定时任务
        taskManager.removeTaskIfPresent(userId);
        message = message.trim();
        //统计页面
        final String statistics = "statistics";
        //监控页面
        final String monitor = "monitor";
        if (message.startsWith(statistics)) {
            //消息格式statistics/yyyy-MM-dd/clusterId
            int start = message.indexOf("/");
            int end = message.lastIndexOf("/");
            String date = message.substring(start + 1, end);
            int clusterId = Integer.parseInt(message.substring(end + 1));
            final List<ServerVo> servers = serverService.getServers(clusterId);
            if (servers.isEmpty()) {
                return WebResult.error("该集群下尚未配置节点信息");
            }
            StatisticsQueryDto dto = new StatisticsQueryDto();
            dto.setUserId(userId);
            dto.setClusterId(clusterId);
            dto.setDate(date);
            StatisticsVo statisticsData = statisticalAnalysisService.getStatisticsData(dto);
            if (Objects.isNull(statisticsData)) {
                return WebResult.error(ResponseMsg.NOT_HAVE_CLUSTER);
            }
            //添加定时任务
            String today = DateUtil.today();
            if (today.equals(date)) {
                taskManager.addStatisticsTask(userId, clusterId);
            }
            return WebResult.ok(statisticsData);
        } else if (message.startsWith(monitor)) {
            //消息格式 monitor/clusterId/serverId
            int start = message.indexOf("/");
            int end = message.indexOf("/", start + 1);
            int clusterId = Integer.parseInt(message.substring(start + 1, end));
            final List<ServerVo> servers = serverService.getServers(clusterId);
            if (servers.isEmpty()) {
                return WebResult.error("该集群下尚未配置节点信息");
            }
            int serverId = Integer.parseInt(message.substring(end + 1));
            MonitorDto dto = new MonitorDto();
            dto.setClusterId(clusterId);
            dto.setServerId(serverId == 0 ? null : serverId);
            WebResult<?> result = WebResult.ok(monitoringAlarmsService.getMonitorData(dto));
            //添加定时任务
            taskManager.addMonitorTask(userId, clusterId, serverId == 0 ? null : serverId);
            return result;
        } else {
            log.warn("不支持的消息协议:{}", message);
            return null;
        }
    }
}
