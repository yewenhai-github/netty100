package com.netty100.timer.config;

/**
 * @Description 任务配置类
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/29
 */
public class TaskConfig {
    /**
     * 心跳时间
     */
    public static final int BEAT_TIMEOUT = 60;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    /**
     * 读取流量的单位数量
     */
    public static final int DEFAULT_READ_UNIT_COUNT = 5;

    /**
     * 默认移除多少天前的数据 10
     * 读取配置
     */
    public static final Integer SERVER_MESSAGE_REMOVE_DAYS = -10;
    public static final Integer SERVER_MESSAGE_REMOVE_COUNT = 1000;

    /**
     * 连接历史日志清理 默认3天
     *
     * @return
     */
    public static final Integer SERVER_CONNECTION_REMOVE_DAYS = -3;

    /**
     * 读取重连的单位数量
     */
    public static final int DEFAULT_READ_RECONNECT_UNIT_COUNT = 5;

    public static String loadEmailJobAlarmTemplate() {
        String mailBodyTemplate = "<h5>告警明细:</span>" +
                "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
                "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
                "      <tr>\n" +
                "         <td width=\"20%\" >告警类型</td>\n" +
                "         <td width=\"80%\" >告警内容</td>\n" +
                "      </tr>\n" +
                "   </thead>\n" +
                "   <tbody>\n" +
                "      <tr>\n" +
                "         <td>{0}</td>\n" +
                "         <td>{1}</td>\n" +
                "      </tr>\n" +
                "   </tbody>\n" +
                "</table>";

        return mailBodyTemplate;
    }

    public static String thresholdContent = "\n 集群: %s\n 服务节点id: %s\n ip: %s\n 阈值: %s\n 检测值:%s\n 超过阈值:%s\n";
    public static String downContent = "\n 集群: %s\n 服务节点id:%s\n ip:%s\n";
}
