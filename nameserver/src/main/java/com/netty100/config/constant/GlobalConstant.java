package com.netty100.config.constant;

import javax.print.DocFlavor;

/**
 * @Description
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/13
 */
public interface  GlobalConstant {
    /**
     * 全局告警配置初始化
     */
    Integer DEFAULT_SERVER_ID = 0;
    String INIT_REPLACE_WORD = "超";
    String STING_EMPTY ="";

    /**
     * 连接日志清理
     */
    String DEFAULT_LOG_TABLE_NAME_PREFIX="api_connection_history";
    Integer DEFAULT_LOG_TABLE_COUNT = 10;
    Integer DEFAULT_READ_PAGE_SIZE = 1000;

    /**
     * 空格分割符串
     */
    String SPLIT_STR = " ";
    String CONCAT_STR = ":00";
    String SPLIT_CLUSTER_SERVER = "/";

    String WARN_STATISTICS_TIME_INTERVAL = "\n 统计起止:";

    String PERCENT_SIGN = "%";

    String EMPTY_STRING  ="";
}
