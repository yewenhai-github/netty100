package com.netty100.kernel.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Component
@Slf4j
public class WhyCountUtils {
    /******************************************【平台统计】*********************************************/
    public static LongAdder platform_c2p_connect_active_total = new LongAdder();//连接维度：客户端连接次数
    public static LongAdder platform_c2p_connect_inactive_total = new LongAdder();//连接维度：客户端正常断开次数
    public static LongAdder platform_c2p_connect_error_total = new LongAdder();//连接维度：客户端异常断开次数
    public static LongAdder platform_c2p_connect_idle_close_total = new LongAdder();//连接维度：客户端心跳断开次数
    public static LongAdder platform_s2p_connect_active_total = new LongAdder();//连接维度：服务器连接次数
    public static LongAdder platform_s2p_connect_inactive_total = new LongAdder();//连接维度：服务器正常断开次数
    public static LongAdder platform_s2p_connect_error_total = new LongAdder();//连接维度：服务器异常断开次数
    public static LongAdder platform_s2p_connect_idle_close_total = new LongAdder();//连接维度：服务器心跳断开次数

    public static LongAdder platform_c2p_message_read_success_total = new LongAdder();//消息维度：客户端投递成功次数
    public static LongAdder platform_c2p_message_read_fail_total = new LongAdder();//消息维度：客户端投递失败次数
    public static LongAdder platform_s2p_message_read_success_total = new LongAdder();//消息维度：服务器投递成功次数
    public static LongAdder platform_s2p_message_read_fail_total = new LongAdder();//消息维度：服务器投递失败次数
    public static LongAdder platform_p2p_message_relay_total = new LongAdder();//消息维度：消息转发次数

    public static LongAdder platform_c2p_message_read_success_flow = new LongAdder();//流量维度：客户端投递成功流量
    public static LongAdder platform_c2p_message_read_fail_flow = new LongAdder();//流量维度：客户端投递失败流量
    public static LongAdder platform_s2p_message_read_success_flow = new LongAdder();//流量维度：服务器投递成功流量
    public static LongAdder platform_s2p_message_read_fail_flow = new LongAdder();//流量维度：服务器投递失败流量
    public static LongAdder platform_p2p_message_relay_flow = new LongAdder();//流量维度：消息转发流量
}
