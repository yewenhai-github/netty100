package com.netty100.cluster.sys.utils;

import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import com.netty100.common.utils.SysUtility;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientCacheUtils {
    private static ConcurrentMap<String, IpPortBasedClient> c2pCacheChannelMap = new ConcurrentHashMap<>();

    public static ConcurrentMap<String, IpPortBasedClient> getClients(){
        return c2pCacheChannelMap;
    }

    public static Channel getChannel(String channelKey){
        IpPortBasedClient ipPortBasedClient = ClientCacheUtils.getClients().get(channelKey);
        if(SysUtility.isEmpty(ipPortBasedClient)){
            return null;
        }
        return ipPortBasedClient.getChannel();
    }
}
