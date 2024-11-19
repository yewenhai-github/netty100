package com.netty100.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;


/**
 * so-easy private
 *
 * @author why 2020-04-22
 * @version 7.0.0
 */
@Slf4j
@SuppressWarnings("ALL")
public class LogUtil {
    public static final Level DEBUG = Level.DEBUG;
    public static final Level INFO = Level.INFO;
    public static final Level WARN = Level.WARN;
    public static final Level ERROR = Level.ERROR;
    public static final Level ALARM = Level.FATAL;

    /**
     * 按照日志配置信息写日志
     *
     * @param logMessage 日志消息
     * @param errorLevel 日志级别
     */
    public static void printLog(String logMessage, Level errorLevel) {
        printLog(logMessage, errorLevel, null);
    }

    /**
     * 按照日志配置信息写日志
     *
     * @param logMessage 日志消息
     * @param e          例外
     */
    public static void printLog(String logMessage, Level level, Throwable e) {
        try {
            if (level == DEBUG) {
                log.debug(logMessage, e);
            } else if (level == INFO) {
                log.info(logMessage, e);
            } else if (level == WARN) {
                log.warn(logMessage, e);
            } else if (level == ERROR) {
                log.error(logMessage, e);
            }
        } catch (Exception ex) {
            log.error("记录日志时发生以下错误：" + ex.getMessage());
        }
    }

}
