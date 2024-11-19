package com.netty100.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.netty100.config.constant.GlobalConstant;
import com.netty100.entity.Cluster;
import com.netty100.entity.Server;
import com.netty100.entity.WarnHistoryInfo;
import com.netty100.enumeration.WarnType;
import com.netty100.pojo.dto.ServerWarnContentDto;
import com.netty100.service.ClusterService;
import com.netty100.service.CommonWarnSendService;
import com.netty100.service.UserService;
import com.netty100.service.WarnInfoService;
import com.netty100.timer.config.TaskConfig;
import com.netty100.utils.DateUtils;
import com.netty100.utils.MailWarnSender;
import com.netty100.utils.MathUtil;
import com.netty100.utils.WarnSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description 对告警进行逻辑封装处理
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/30
 */
@Slf4j
@Service
public class CommonWarnSendServiceImpl implements CommonWarnSendService {
    @Autowired
    private List<WarnSender> warnSenders;
    @Autowired
    private UserService userService;
    @Autowired
    private WarnInfoService warnInfoService;
    @Autowired
    private ClusterService clusterService;

    @Override
    public void warnServerSend(List<ServerWarnContentDto> servers, WarnType warnType, List<String> times) {
        String content = "";
        if (CollectionUtil.isNotEmpty(times)) {
            content = content
                    .concat(GlobalConstant.WARN_STATISTICS_TIME_INTERVAL)
                    .concat(times.get(0)
                            .concat("-")
                            .concat(times.get(times.size() - 1)));
        }
        content = content.concat(servers.stream()
                .map(ServerWarnContentDto::getWarnContent)
                .collect(Collectors.joining(" ")));
        // 异步告警
        String finalContent = content;
        log.info("告警类型:{},告警内容:{}", warnType.getTitle(), finalContent);
        doSend(warnType, finalContent);
        servers.forEach(server -> saveWarnInfo(server.getServer(),
                finalContent, warnType, null));
    }

    @Override
    public void warnClusterSend(List<Cluster> clusters, WarnType warnType) {
        String content = clusters
                .stream()
                .map(Cluster::getCluster)
                .collect(Collectors.joining(","))
                .concat(warnType.getTitle()).concat("\n");
        doSend(warnType, content);
        clusters.forEach(cluster -> saveWarnInfo(null, content, warnType, cluster.getId()));
    }

    private int saveWarnInfo(Server server, String content, WarnType warnType, Integer clusterId) {
        WarnHistoryInfo warnInfo = new WarnHistoryInfo();
        warnInfo.setCreateTime(LocalDateTime.now());
        warnInfo.setDetail(content);
        if (Objects.isNull(server)) {
            warnInfo.setClusterId(clusterId);
        } else {
            warnInfo.setServerId(server.getId());
            warnInfo.setClusterId(server.getClusterId());
        }
        warnInfo.setWarnLevel(warnType.getWarnLevel().getLevel());
        warnInfo.setCreateDay(DateUtils.getFormatNow(DateUtils.YMD_DASH));
        warnInfo.setWarnType(warnType.getIndex());
        return warnInfoService.add(warnInfo);
    }

    public void doSend(WarnType warnType, String content) {
        if (CollectionUtil.isNotEmpty(warnSenders)) {
            // 获取接收告警的人群
            List<Integer> acceptUsers = userService.getAcceptUserIds();
            warnSenders.forEach(warnSender -> {
                if (warnSender instanceof MailWarnSender) {
                    String mailContent = MessageFormat
                            .format(TaskConfig.loadEmailJobAlarmTemplate(), warnType.getTitle(), content);
                    warnSender.sendWarn(acceptUsers, warnType.getTitle(), mailContent);
                    return;
                }
                warnSender.sendWarn(acceptUsers, warnType.getTitle(), content);
            });
        }
    }

    @Override
    public ServerWarnContentDto warpWarn(Server server, BigDecimal warnThreshold, BigDecimal averageStatistics, WarnType warnType) {
        List<Integer> rateList = Arrays.asList(WarnType.CLIENT_READ_FAILED_RATE.getIndex(), WarnType.KERNEL_FORWARD_RATE.getIndex());
        BigDecimal subtract = averageStatistics.subtract(warnThreshold);
        Cluster cluster = clusterService.getById(server.getClusterId());
        String prettyWarnThreshold = MathUtil.getPrettyNumber(warnThreshold).toPlainString();
        String prettyAverageStatistics = MathUtil.getPrettyNumber(averageStatistics).toPlainString();
        String prettySubtract = MathUtil.getPrettyNumber(subtract).toPlainString();
        if (rateList.contains(warnType.getIndex())) {
            prettyWarnThreshold = prettyWarnThreshold.concat(GlobalConstant.PERCENT_SIGN);
            prettyAverageStatistics = prettyAverageStatistics.concat(GlobalConstant.PERCENT_SIGN);
            prettySubtract = prettySubtract.concat(GlobalConstant.PERCENT_SIGN);
        }
        String content = String.format(
                TaskConfig.thresholdContent,
                Objects.nonNull(cluster) ? cluster.getCluster() : GlobalConstant.EMPTY_STRING,
                server.getId(),
                server.getIntranetIp(),
                prettyWarnThreshold,
                prettyAverageStatistics,
                prettySubtract);
        return new ServerWarnContentDto(server, content);
    }
}
