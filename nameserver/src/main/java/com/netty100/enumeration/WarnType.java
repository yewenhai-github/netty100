package com.netty100.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

/**
 * 告警类型
 *
 * @author why
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WarnType {

    /**
     * App端 告警信息枚举values
     */
    CLIENT_CONNECTION_TOO_MANY("client", "游戏端连接量超阈值", 1, WarnLevel.MOST_URGENT,"C端连接量"),
    CLIENT_ERROR_DISCONNECT_COUNT("client", "游戏端异常断开次数超阈值", 2, WarnLevel.COMMON,"C端异常断开量"
    ),
    //    CLIENT_IDLE_DISCONNECT_COUNT("App端", "游戏端心跳断开次数超阈值", 3),
    CLIENT_READ_FAILED_COUNT("client", "游戏端投递失败次数超阈值", 4, WarnLevel.SERIOUS,"C端投递失败量"),
    CLIENT_READ_FAILED_SIZE("client", "游戏端投递失败流量超阈值", 5, WarnLevel.SERIOUS,"C端投递失败流"),
    CLIENT_ERROR_RECONNECT_TIMES("client", "游戏端失败重连次数超阈值", 6, WarnLevel.SERIOUS,"C端失败重连量"),
    CLIENT_IDLE_RECONNECT_TIMES("client", "游戏端心跳重连次数超阈值", 7, WarnLevel.COMMON,"C端心跳重连量"),
    CLIENT_READ_FAILED_RATE("kernel", "消息投递失败率超阈值", 8, WarnLevel.SERIOUS,"N端投递失败率"),
//    CLIENT_READ_FAILED_RATE("App端","游戏端消息投递失败率超阈值",8),
    /**
     * 服务器
     */

    SERVER_READ_FAILED_COUNT("server", "服务器投递失败次数超阈值", 9, WarnLevel.USUAL_URGENT,"S端投递失败量"),
    SERVER_READ_FAILED_SIZE("server", "服务器投递失败流量超阈值", 10, WarnLevel.USUAL_URGENT,"S端投递失败流"),
    SERVER_ERROR_DISCONNECT_COUNT("server", "服务器异常断开次数超阈值", 11, WarnLevel.COMMON,"S端异常断开量"),

    /**
     * 内核
     */
    KERNEL_FORWARD_RATE("kernel", "消息转发率超阈值", 12, WarnLevel.USUAL_URGENT,"N端转发率"),
    KERNEL_FORWARD_COUNT("kernel", "消息转发次数超阈值", 13, WarnLevel.USUAL_URGENT,"N端转发次数"),
    KERNEL_TRAFFIC_COUNT("kernel", "消息次数超阈值", 14, WarnLevel.USUAL_URGENT,"N端消息量"),
    KERNEL_TRAFFIC_SIZE("kernel", "消息量超阈值", 15, WarnLevel.USUAL_URGENT,"N端消息流"),


    SERVER_DOWN("", "服务节点宕机", 16, WarnLevel.USUAL_URGENT,"节点宕机"),
    CLUSTER_DOWN("", "集群不可用", 17, WarnLevel.MOST_URGENT,"集群离线");

    public static void main(String[] args) throws JsonProcessingException {
        val objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(KERNEL_FORWARD_RATE));
    }

    WarnType(String group, String title, int index, WarnLevel warnLevel,String shortName) {
        this.group = group;
        this.title = title;
        this.index = index;
        this.warnLevel = warnLevel;
        this.shortName = shortName;
    }


    private final String title;
    private final int index;
    private final String group;
    private final WarnLevel warnLevel;
    private final String shortName;

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public String getGroup() {
        return group;
    }

    public WarnLevel getWarnLevel() {
        return warnLevel;
    }

    public String getShortName() {
        return shortName;
    }
}
