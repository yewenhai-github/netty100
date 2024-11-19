package com.netty100.netty.server.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netty100.common.chooser.ChooserSelector;
import com.netty100.common.exception.CommonException;
import com.netty100.common.http.GsonTool;
import com.netty100.common.http.RemotingHttpResult;
import com.netty100.common.http.RemotingHttpUtil;
import com.netty100.common.properties.WhyNettyCloudProperties;
import com.netty100.common.protocol.WhyMessageFixedHeader;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import com.netty100.netty.server.connect.ClientReconnect;
import com.netty100.netty.server.service.WhyNettyChannelService;
import com.netty100.netty.server.service.WhyNettySdkService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
@Slf4j
public class WhyServerUtils {
    private volatile static List<Channel> cacheChannelList = new ArrayList<>(); //Collections.synchronizedList(new ArrayList<>());
    private volatile static Map<ChannelId, String> channelIdMap = new ConcurrentHashMap();
    private volatile static Map<String, Integer> channelIpMap = new ConcurrentHashMap();

    public static Channel getCacheChannel() {
        if(SysUtility.isEmpty(cacheChannelList)){
            log.info("当前管道为空，正在努力重新重连中...");
            WhySpringUtils.getBean(ClientReconnect.class).doCommand();
        }
        if(SysUtility.isEmpty(cacheChannelList)){
            throw new CommonException("服务器管道获取失败，当前管道为空");
        }
        return (Channel) ChooserSelector.getLoopChooser().next(cacheChannelList);
    }

    public static void channelActive(Long userId, ChannelHandlerContext ctx){
        if(SysUtility.isEmpty(userId) || SysUtility.isEmpty(WhySpringUtils.getBean(WhyNettyChannelService.class))){
            return;
        }
        WhySpringUtils.getBean(WhyNettyChannelService.class).channelActive(userId, ctx);
    }

    public static void channelInactive(Long userId, ChannelHandlerContext ctx){
        if(SysUtility.isEmpty(userId) || SysUtility.isEmpty(WhySpringUtils.getBean(WhyNettyChannelService.class))){
            return;
        }
        WhySpringUtils.getBean(WhyNettyChannelService.class).channelInactive(userId, ctx);
    }

    public static void channelIdleInactive(Long userId, ChannelHandlerContext ctx){
        if(SysUtility.isEmpty(WhySpringUtils.getBean(WhyNettyChannelService.class))){
            return;
        }
        WhySpringUtils.getBean(WhyNettyChannelService.class).channelIdleInactive(userId, ctx);
    }

    public static void exceptionCaught(Long userId, ChannelHandlerContext ctx){
        if(SysUtility.isEmpty(userId) || SysUtility.isEmpty(WhySpringUtils.getBean(WhyNettyChannelService.class))){
            return;
        }
        WhySpringUtils.getBean(WhyNettyChannelService.class).exceptionCaught(userId, ctx);
    }

    public static void doCommand(byte[] msg, WhyMessageFixedHeader header, Channel channel){
        if(SysUtility.isEmpty(msg) || SysUtility.isEmpty(WhySpringUtils.getBean(WhyNettySdkService.class))){
            return;
        }
        WhySpringUtils.getBean(WhyNettySdkService.class).doCommand(msg, header);
    }

    public static List<Channel> getCacheChannelList(){
        return cacheChannelList;
    }

    public static Map<ChannelId, String> getChannelIdMap(){
        return channelIdMap;
    }

    public static Map<String, Integer> getChannelIpMap(){
        return channelIpMap;
    }

    public static String closeCtx(Channel channel){
        String channelKey = null;
        try {
            channelKey = getChannelKey(channel);
            if(SysUtility.isEmpty(channelKey)){
                return null;
            }

            channelDelele(channelKey, channel);
        } catch (Exception e) {
            log.error("连接异常断开", e);
        } finally {
            channel.close();
        }
        return channelKey;
    }

    public static String getChannelKey(Channel channel) {
        return channelIdMap.get(channel.id());
    }

    /**
     * 用户连接接入缓存
     * @param ctx
     */
    public static synchronized void channelCache(ChannelHandlerContext ctx) {
        String channelKey = "s"+SysUtility.getHostIp() + "_p"+ getRemoteIp(ctx.channel()) + "_" + (System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L);
        //缓存客户端连接的Channel
        boolean rt = doCacheChannelList(add, channelKey, ctx.channel());
        if(rt){
            log.info("用户激活成功，channelKey={}，当前管道数={}", channelKey, cacheChannelList.size());
        }else{
            log.error("用户激活失败，channelKey={}，当前管道数={}，原因未知", channelKey, cacheChannelList.size());
        }
    }

    public static String getRemoteIp(Channel channel){
        InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
        if(SysUtility.isEmpty(insocket)){
            return "-1";
        }
        String remoteIp = SysUtility.isNotEmpty(insocket.getAddress()) ? insocket.getAddress().getHostAddress() : "-1";
        return remoteIp;
    }

    public static void channelDelele(String channelKey, Channel c) {
        if(SysUtility.isEmpty(channelKey)){
            return;
        }

        Channel channel = null;
        try {
            doCacheChannelList(delete, channelKey, c);
        } catch (Exception e) {
            log.error("用户{}下线失败", channelKey, e);
        } finally {
            if(SysUtility.isNotEmpty(channel)){
                channel.close();
            }
        }
    }

    private static final String delete = "delete";
    private static final String add = "add";
    private synchronized static boolean doCacheChannelList(String type, String channelKey, Channel channel){
        if(add.equalsIgnoreCase(type)){
            //缓存channelId，用于关闭管道时同步清空客户端连接缓存c2pCacheChannelMap
            channelIdMap.put(channel.id(), channelKey);
            //缓存连接数统计
            Integer activeChannelIpCount = SysUtility.isEmpty(channelIpMap.get(getRemoteIp(channel))) ? 1: channelIpMap.get(getRemoteIp(channel)) + 1;
            channelIpMap.put(getRemoteIp(channel), activeChannelIpCount);

            return cacheChannelList.add(channel);
        }else if(delete.equalsIgnoreCase(type)){
            for (int i = 0; i < cacheChannelList.size(); i++) {
                Channel c = cacheChannelList.get(i);
                if(c.id() == channel.id()){
                    channelIdMap.remove(c.id());
                    channelIpMap.put(getRemoteIp(c), channelIpMap.get(getRemoteIp(c)) - 1);

                    return cacheChannelList.remove(c);
                }
            }
        }else{
            log.error("不支持的操作类型（cacheChannelList）");
        }
        return false;
    }

    public static String getCloudServerIps(WhyNettyCloudProperties whyNettyCloudProperties){
        String cluster = whyNettyCloudProperties.getCluster();
        List<String> list = new ArrayList<>();
        JSONObject params = new JSONObject();
        params.put("cluster", cluster);
        RemotingHttpResult rt = WhyServerUtils.postBody(whyNettyCloudProperties, "/api/nameserver/server/list", params);
        if(200 == rt.getResponseCode()){
            JSONObject jsonObject = JSONObject.parseObject(rt.getResponseStr());
            JSONArray datas = (JSONArray)jsonObject.get("data");
            if(SysUtility.isNotEmpty(datas)){
                for (int i = 0; i < datas.size(); i++) {
                    JSONObject data = (JSONObject)datas.get(i);
                    String intranetIp = data.getString("intranetIp");
                    if(Objects.nonNull(intranetIp)){
                        list.add(intranetIp);
                    }
                }
            }else{
                log.error("静态地址获取失败，请求参数{}，请求结果{}", GsonTool.toJson(params), rt.getResponseStr());
            }
        }
        return String.join(",", list);
    }

    public static RemotingHttpResult postBody(WhyNettyCloudProperties whyNettyCloudProperties, String uri, Object requestObj){
        RemotingHttpResult rt = new RemotingHttpResult();
        rt = RemotingHttpUtil.postBody(whyNettyCloudProperties.getDomain() + uri, whyNettyCloudProperties.getToken(), whyNettyCloudProperties.getTimeOut(), requestObj);
        //打印一下非200的错误请求日志
        if(rt.getResponseCode() != 200){
            log.error("http请求失败：{}\n"+"请求信息={}\n返回信息={}"
                    , whyNettyCloudProperties.getDomain() + uri
                    , GsonTool.toJson(requestObj), rt.getResponseStr());
        }
        return rt;
    }

    public static Channel getCurrentChannel(){
        Channel channel = SysUtility.getCurrentProducerChannel();
        if(SysUtility.isEmpty(channel)){
            channel = getCacheChannel();
        }
        return channel;
    }


}
