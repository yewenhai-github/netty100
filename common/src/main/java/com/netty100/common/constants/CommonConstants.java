package com.netty100.common.constants;

/**
 * 通用变量
 */
public class CommonConstants {
    //sdk服务器管道线程变量
    public final static String CURRENT_CHANNEL ="currentChannel";
    public final static String CURRENT_MESSAGE_ID ="currentMessageId";
    //消息协议固定长度
    public final static int MESSAGE_FIXED_HEADER_LENGTH = 29;
    public final static short MESSAGE_VARIABLE_HEADER_LENGTH_V0 = 8;

    /****************异常、默认的报文类型*******************/
    public final static byte way_error_channelActive = -1;
    public final static byte way_default_channelActive = 0;
    /****************客户端 -> 平台 ->后端业务*******************/
    public final static byte way_c2p_channelActive = 1;
    public final static byte way_c2p_channelRead0 = 3;
    public final static byte way_c2p_idleState = 5;
    public final static byte way_c2p_channelInactive = 7;
    public final static byte way_c2p_exceptionCaught = 9;
    /****************后端业务 -> 平台 ->客户端*******************/
    public final static byte way_s2p_channelActive = 2;
    public final static byte way_s2p_channelRead0 = 4;
    public final static byte way_s2p_idleState = 6;
    public final static byte way_s2p_channelInactive = 8;
    public final static byte way_s2p_exceptionCaught = 10;
    /****************后端Job -> 平台 ->客户端*******************/
    public final static byte way_j2p_channelActive = 11;
    public final static byte way_j2p_channelRead0 = 13;
    public final static byte way_j2p_idleState = 15;
    public final static byte way_j2p_channelInactive = 17;
    public final static byte way_j2p_exceptionCaught = 19;

    public final static byte way_m2p_channelActive = 12;
    public final static byte way_m2p_channelRead0 = 14;
    public final static byte way_m2p_idleState = 16;
    public final static byte way_m2p_channelInactive = 18;
    public final static byte way_m2p_exceptionCaught = 20;

    public final static byte way_simplex_channelActive = 21;
    public final static byte way_simplex_channelRead0 = 22;
    public final static byte way_simplex_idleState = 23;
    public final static byte way_simplex_channelInactive = 24;
    public final static byte way_simplex_exceptionCaught = 25;


    public final static byte type_online_single_message = 1;
    public final static byte type_online_all_message = 2;
    public final static byte type_offline_single_message = 3;
    public final static byte type_offline_all_message = 4;
    public final static byte type_online_broadcast_message = 5;
    public final static byte type_online_relay_point_message = 6;
    public final static byte type_online_relay_all_message = 7;
    public final static byte type_offline_client_user = 8;


    //传输模式 全双工 半双工 单工
    public final static int TRANSFER_MODE_FULL_DUPLEX = 1;
    public final static int TRANSFER_MODE_HALF_DUPLEX = 2;
    public final static int TRANSFER_MODE_SIMPLEX = 3;

    //默认值
    public final static byte DEFAULT_MESSAGE_SOURCE = -1;
    public final static byte DEFAULT_MESSAGE_DEST = -1;
    public final static byte DEFAULT_MESSAGE_SERIALIZE = -1;
    public final static byte DEFAULT_MESSAGE_WAY = -1;
    public final static byte DEFAULT_MESSAGE_TYPE = 1; //WhyMessageCode.type_online_single_message.getCode()
    public final static Long DEFAULT_DEVICE_ID = -1L;

    public final static Long DEFAULT_USER_ID = -1L;
    // 可变头默认值
    public final static byte DEFAULT_VARIABLE_API_VERSION = 0;
    public final static boolean DEFAULT_IS_TWO_WAY_MSG = true;
    public final static boolean DEFAULT_IS_FIRST_TIME = true;
    public final static boolean DEFAULT_VARIABLE_IS_REWRITE = true;
    public final static int DEFAULT_EXPIRES_TIME = 0;


    public final static int CONNECT_EXECUTOR = 1;
    public final static int CLIENT_EXECUTOR = 2;
    public final static int SERVER_EXECUTOR = 3;
    public final static int PING_EXECUTOR = 4;


    public static final int CLIENT_TYPE_WEB = 1;
    public static final int CLIENT_TYPE_JOB = 2;
    public static final int CLIENT_TYPE_MQ = 3;
}
