package com.netty100.broker.protocol;

public class TimerRunConstants {

    public static volatile boolean statisticRegisterRuning = false;
    public static volatile boolean nameServerListRuning = false;
    public static volatile boolean nameServerStartRuning = false;
    public static volatile boolean nameServerRegisterRuning = false;
    public static volatile boolean registerClientChannelActiveRuning = false;
    public static volatile boolean registerClientChannelInactiveRuning = false;
    public static volatile boolean registerClientChannelExceptionCaughtRuning = false;
    public static volatile boolean registerClientChannelIdleInactiveRuning = false;
    public static volatile boolean registerServerChannelActiveRuning = false;
    public static volatile boolean registerServerChannelInactiveRuning = false;
    public static volatile boolean registerServerChannelExceptionCaughtRuning = false;
    public static volatile boolean registerClientMessageLogRuning = false;

    public static final Integer HTTP_CODE_SUCCESS = 200;
    public static final Integer HTTP_CODE_FAIL = 400;

    public static final Integer BATCH_NUM = 1000;
    public static final long WHEEL_TIMEOUT_MILLIS = 30000;
    public static final Integer WHEEL_DELAY = 3000;
    public static final Integer WHEEL_DELAY_LEVEL1 = 1000;
}
