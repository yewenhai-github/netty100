package com.netty100.utils;

import cn.hutool.core.collection.CollectionUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Description 日期工具类
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/3/30
 */
public class DateUtils {

    private static ThreadLocal<Map<String, SimpleDateFormat>> threadLocal = new ThreadLocal<>();

    public static final String HHMM = "HH:mm";
    public static final String YMD_DASH = "yyyy-MM-dd";
    public static final String YMD_DASH_BLANK_HMS_COLON = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD_DASH_BLANK_DAY_END_COLON = "yyyy-MM-dd 23:59:59";
    public static final String YMD_DASH_BLANK_HM_START_COLON = "yyyy-MM-dd HH:mm";

    private static final int ONE_MIN = 1 * 60 * 1000;

    public static List<String> getHmMBeforeTimes(int beforeTime) {
        Date now = new Date();
        // 计算前beforeTime分钟间隔
        List<String> timeList = new ArrayList<>();
        Date begin = new Date(now.getTime() - beforeTime * ONE_MIN + 30);
        while (begin.before(now)) {
            String time = getFormat(HHMM).format(begin);
            timeList.add(time);
            begin.setTime(begin.getTime() + ONE_MIN);
        }
        // 上报时间 与当前时间差2分钟 因此统计时间段应该从beforeTime + 1 开始到now -1截止
        if (CollectionUtil.isNotEmpty(timeList)){
            timeList.remove(timeList.size()-1);
        }
        return timeList;
    }

    /**
     * 获取当前时间的格式化字符串
     *
     * @param pattern
     * @return 格式化字符串
     */
    public static String getFormatNow(String pattern) {
        return getFormat(pattern, Locale.getDefault()).format(new Date());
    }

    /**
     * 根据特定格式获取SimpleDateFormat
     *
     * @param pattern
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getFormat(String pattern) {
        return getFormat(pattern, Locale.getDefault());
    }

    /**
     * 根据特定格式获取SimpleDateFormat
     *
     * @param pattern
     * @param locale
     * @return
     */
    public static SimpleDateFormat getFormat(String pattern, Locale locale) {
        Map<String, SimpleDateFormat> formatMap = threadLocal.get();
        SimpleDateFormat format;
        if (formatMap == null) {
            formatMap = new HashMap<>();
            format = new SimpleDateFormat(pattern, locale);
            formatMap.put(pattern, format);
            threadLocal.set(formatMap);
        } else {
            format = formatMap.get(pattern);
            if (format == null) {
                format = new SimpleDateFormat(pattern, locale);
                formatMap.put(pattern, format);
            }
        }
        return format;
    }

    /**
     * 根据格式化方式获取前一天的日期
     *
     * @return
     */
    public static String formatYMDPlusDay(Integer plusDays, String pattern) {
        return LocalDateTime.now().plusDays(plusDays).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 通过给定格式化获取前后N分钟的时间
     *
     * @param minute
     * @param pattern
     * @return
     */
    public static String formatYMDPlusMinute(Integer minute, String pattern) {
        return LocalDateTime.now().plusMinutes(minute).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String stringDatePlusMinute(String time,Integer plusMinute){
        LocalDateTime onDutyBegin =
                LocalDateTime.parse(time, DateTimeFormatter.ofPattern(YMD_DASH_BLANK_HMS_COLON));
        return onDutyBegin.plusMinutes(plusMinute).format(DateTimeFormatter.ofPattern(YMD_DASH_BLANK_HMS_COLON));
    }
    public static void main(String[] args) {
        System.out.println(getHmMBeforeTimes(5));
    }
}
