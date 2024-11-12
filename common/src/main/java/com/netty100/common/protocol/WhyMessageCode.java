package com.netty100.common.protocol;

import com.netty100.common.constants.CommonConstants;

public enum WhyMessageCode {
    way_error_channelActive(CommonConstants.way_error_channelActive),
    way_default_channelActive(CommonConstants.way_default_channelActive),
    way_c2p_channelActive(CommonConstants.way_c2p_channelActive),
    way_c2p_channelRead0(CommonConstants.way_c2p_channelRead0),
    way_c2p_idleState(CommonConstants.way_c2p_idleState),
    way_c2p_channelInactive(CommonConstants.way_c2p_channelInactive),
    way_c2p_exceptionCaught(CommonConstants.way_c2p_exceptionCaught),
    way_s2p_channelActive(CommonConstants.way_s2p_channelActive),
    way_s2p_channelRead0(CommonConstants.way_s2p_channelRead0),
    way_s2p_idleState(CommonConstants.way_s2p_idleState),
    way_s2p_channelInactive(CommonConstants.way_s2p_channelInactive),
    way_s2p_exceptionCaught(CommonConstants.way_s2p_exceptionCaught),
    way_simplex_channelActive(CommonConstants.way_simplex_channelActive),
    way_simplex_channelRead0(CommonConstants.way_simplex_channelRead0),
    way_simplex_idleState(CommonConstants.way_simplex_idleState),
    way_simplex_channelInactive(CommonConstants.way_simplex_channelInactive),
    way_simplex_exceptionCaught(CommonConstants.way_simplex_exceptionCaught),

    serialize_string((byte) 1),
    serialize_protobuf2((byte) 2),
    serialize_protobuf3((byte) 3),
    serialize_json((byte) 4),

    source_default((byte) 0),
    dest_default((byte) 0),

    type_online_single_message(CommonConstants.type_online_single_message),
    type_online_all_message(CommonConstants.type_online_all_message),
    type_offline_single_message(CommonConstants.type_offline_single_message),
    type_offline_all_message(CommonConstants.type_offline_all_message),
    type_online_broadcast_message(CommonConstants.type_online_broadcast_message),
    type_online_relay_point_message(CommonConstants.type_online_relay_point_message),
    type_online_relay_all_message(CommonConstants.type_online_relay_all_message),
    type_offline_client_user(CommonConstants.type_offline_client_user)
    ;

    private byte code;

    WhyMessageCode(byte code) {
        this.code = code;
    }

    public static WhyMessageCode valueOf(byte code) {
        for (WhyMessageCode languageCode : WhyMessageCode.values()) {
            if (languageCode.getCode() == code) {
                return languageCode;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}
