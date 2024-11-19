package com.netty100.timer;

import cn.hutool.core.collection.CollectionUtil;
import com.netty100.entity.Cluster;
import com.netty100.enumeration.WarnType;
import com.netty100.pojo.vo.ForwardRateDetectionVo;
import com.netty100.service.*;
import com.netty100.timer.config.TaskConfig;
import com.netty100.utils.MailWarnSender;
import com.netty100.utils.MathUtil;
import com.netty100.utils.WarnSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author why
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ForwardMessageRateCheckTask {

    private final ClusterService clusterService;

    private final ServerService serverService;

    private final WarnConfigService warnConfigService;

    private final List<WarnSender> warnSenders;

    private final UserService userService;

    private final String warnMessageTemplate = "\n 集群: %s\n 阈值: %s%%\n 检测值:%s%%\n";


    private final ServerTrafficService serverTrafficService;

    @Scheduled(cron = "55 */1 * * * ?")
    @SchedulerLock(name = "forwardMessageRateCheckTask", lockAtMostFor = 40000, lockAtLeastFor = 40000)
    public void forwardMessageRateCheckTask() {
        val start = System.currentTimeMillis();
        log.info("集群消息转发率检测任务开始执行");
        val warnConfig = warnConfigService.getOne(WarnType.KERNEL_FORWARD_RATE.getGroup(), WarnType.KERNEL_FORWARD_RATE.getTitle().replace("超", ""), 0);
        if (Objects.isNull(warnConfig) || warnConfig.getTypeThreshold().equals(BigDecimal.ZERO)) {
            return;
        }
        val dateTime = DateUtils.addMinutes(new Date(), -1);
        val format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        val timeArr = format.format(dateTime).split(" ");
        final List<Cluster> clusters = clusterService.queryAll();
        val stringBuilder = new StringBuilder();
        clusters.stream().filter(cluster -> serverService.getTotalServerCountByClusterId(cluster.getId()) > 0)
                .forEach(cluster -> {
                    val forwardRate = getForwardRate(cluster.getId(), timeArr[0], timeArr[1]);
                    log.info("检测集群:{}消息转发率,检测值:{},阈值:{}", cluster.getCluster(), forwardRate, warnConfig.getTypeThreshold());
                    if (forwardRate.compareTo(warnConfig.getTypeThreshold()) >= 0) {
                        stringBuilder.append(String.format(warnMessageTemplate, cluster.getCluster(), warnConfig.getTypeThreshold(), forwardRate));
                    }
                });
        val content = stringBuilder.toString();
        if (StringUtils.hasText(content)) {
            doSend(WarnType.KERNEL_FORWARD_RATE.getTitle(), stringBuilder.toString());
        }
        val end = System.currentTimeMillis();
        log.info("集群消息转发率检测任务执行完成,耗时:{}ms", end - start);
    }

    private BigDecimal getForwardRate(Integer clusterId, String createDate, String createTime) {
        ForwardRateDetectionVo vo = serverTrafficService.getForwardRateVo(clusterId, createDate, createTime);
        return MathUtil.reserveTwoDigits(vo.getForwardTimes(), vo.getS2pTotalTimes() + vo.getC2pTotalTimes());

    }


    public void doSend(String title, String content) {
        if (CollectionUtil.isNotEmpty(warnSenders)) {
            List<Integer> acceptUsers = userService.getAcceptUserIds();
            warnSenders.forEach(warnSender -> {
                if (warnSender instanceof MailWarnSender) {
                    String mailContent = MessageFormat
                            .format(TaskConfig.loadEmailJobAlarmTemplate(), title, content);
                    warnSender.sendWarn(acceptUsers, title, mailContent);
                    return;
                }
                warnSender.sendWarn(acceptUsers, title, content);
            });
        }
    }
}
