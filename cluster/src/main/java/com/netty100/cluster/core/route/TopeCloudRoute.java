package com.netty100.cluster.core.route;

import com.netty100.common.protocol.RequestCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author why
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
public class TopeCloudRoute {

    public static Map<String, String> uriMap = new HashMap<String, String>();
    static {
        uriMap.put(RequestCode.Req01.getCode(), "/api/nameserver/protocol/list");
//        uriMap.put(RequestCode.Req02.getCode(), "");
        uriMap.put(RequestCode.Req03.getCode(), "/api/nameserver/node/list");
        uriMap.put(RequestCode.Req04.getCode(), "/api/node/boot");
        uriMap.put(RequestCode.Req05.getCode(), "/api/node/heartbeat");
        uriMap.put(RequestCode.Req06.getCode(), "/api/node/shutdown");
        uriMap.put(RequestCode.Req07.getCode(), "/api/node/report");

        uriMap.put(RequestCode.Req11.getCode(), "/api/conn/client/add");
        uriMap.put(RequestCode.Req12.getCode(), "/api/conn/client/delete");
        uriMap.put(RequestCode.Req13.getCode(), "/api/conn/client/error");
        uriMap.put(RequestCode.Req14.getCode(), "/api/conn/client/heartbeat");
        uriMap.put(RequestCode.Req15.getCode(), "/api/conn/server/add");
        uriMap.put(RequestCode.Req16.getCode(), "/api/conn/server/delete");
        uriMap.put(RequestCode.Req17.getCode(), "/api/conn/server/error");
        uriMap.put(RequestCode.Req18.getCode(), "/api/client/channel/report");
        uriMap.put(RequestCode.Req19.getCode(), "/api/message/report");
        uriMap.put(RequestCode.Req20.getCode(), "/api/kernel/log/report");
        uriMap.put(RequestCode.Req21.getCode(), "/api/client/heartbeat/log/report");
        uriMap.put(RequestCode.Req22.getCode(), "/api/server/heartbeat/log/report");
        uriMap.put(RequestCode.Req23.getCode(), "/api/nameserver/node/registration-code");
        uriMap.put(RequestCode.Req24.getCode(), "/api/nameserver/app-config/list");

    }
}
