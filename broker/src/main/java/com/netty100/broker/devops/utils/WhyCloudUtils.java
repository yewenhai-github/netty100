package com.netty100.broker.devops.utils;

import com.netty100.cluster.core.cluster.Member;
import com.netty100.cluster.core.cluster.ServerMemberManager;
import com.netty100.cluster.core.route.TopeCloudRoute;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.entity.ChannelDto;
import com.netty100.broker.devops.queue.WhyMessageQueue;
import com.netty100.broker.protocol.LogPointCode;
import com.netty100.common.http.GsonTool;
import com.netty100.common.http.RemotingHttpResult;
import com.netty100.common.http.RemotingHttpUtil;
import com.netty100.common.protocol.RequestCode;
import com.netty100.common.utils.WhySpringUtils;
import com.netty100.common.utils.SysUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author why
 * @version 1.0.0, 2022/4/1
 * @since 1.0.0, 2022/4/1
 */
@Slf4j
@Component
public class WhyCloudUtils {
    public static String clusterId = "0";
    public static final String separator_ = "_";
    public static final String separator = ":";
    public static Set<String> serviceConfigs = new TreeSet<>();

    @Autowired
    public ServerMemberManager serverMemberManager;

    /**
     * 上报LinkedBlockingQueue内的数据，上报数据统一入口
     * @param blockingQueue
     * @param requestCode
     * @param whyKernelProperties
     * @param whyKernelProperties
     * */
    public static void registerQueue(LinkedBlockingQueue<Object> blockingQueue, String requestCode, WhyKernelProperties whyKernelProperties){
        Object entity = blockingQueue.peek();
        if (SysUtility.isNotEmpty(entity)) {
            // 获取数据
            List<Object> entityList = new ArrayList<Object>();
            blockingQueue.drainTo(entityList);
            // 执行上报
            ChannelDto dto = new ChannelDto();
            dto.setIntranetIp(SysUtility.getHostIp());
            dto.setPort(whyKernelProperties.getPort()+"");
            dto.setProperties(entityList.toArray());
            postBody(whyKernelProperties, requestCode, dto, 1);
        }
    }

    /**
     * 公共上报接口，对接一体化平台的方法入口
     * @param whyKernelProperties
     * @param requestCode
     * @param requestObj
     * */
    public static RemotingHttpResult postBody(WhyKernelProperties whyKernelProperties, String requestCode, Object requestObj, int desc){
        return postBody(whyKernelProperties.getDomain(), whyKernelProperties.getToken(), whyKernelProperties.getTimeOut(), requestCode, requestObj, desc);
    }

    public static RemotingHttpResult postBody(String domain,String token, int timeout, String requestCode, Object requestObj, int desc){
        RemotingHttpResult rt = new RemotingHttpResult();
        String uri = TopeCloudRoute.uriMap.get(requestCode);
        //1. 校验uri
        if(SysUtility.isEmpty(uri)){
            log.error("该接口未定义:{}", requestCode);
            WhyMessageQueue.pushKernelMessageLogQueue(null, LogPointCode.K02.getCode(), LogPointCode.K02.getMessage() +"{"+desc+"}" + requestCode);

            //平台定义的未知错误码
            rt.setResponseCode(999);
            rt.setResponseStr("该接口未定义.");
            return rt;
        }
        //2. 调用请求
        try {
            rt = RemotingHttpUtil.postBody(domain + uri, token, timeout, requestObj, 0);
        } catch (Exception e) {
            log.error("Http IOException, uri={}", domain + uri, e);
            WhyMessageQueue.pushKernelMessageLogQueue(null, LogPointCode.K04.getCode(), LogPointCode.K04.getMessage() + domain + uri + "{"+desc+"}" +  e);

            rt.setResponseCode(998);
            rt.setResponseStr("该接口调用失败.");
            return rt;
        }
        //3. 打印一下非200的错误请求日志
        if(rt.getResponseCode() != 200){
            log.error("http请求失败：{}-{}-{}-{}\n"+"请求信息={}\n返回信息={}"
                    , requestCode, RequestCode.getMassageByCode(requestCode), domain + uri
                    , desc
                    , GsonTool.toJson(requestObj)
                    , rt.getResponseStr());

            StringBuffer error = new StringBuffer();
            error.append("access cloud error，http请求失败："+requestCode+"-"+ RequestCode.getMassageByCode(requestCode)+"-"+domain + uri+"\n");
            error.append("请求信息="+GsonTool.toJson(requestObj)+"\n");
            if(SysUtility.isNotEmpty(rt.getResponseStr()) && rt.getResponseStr().length() > 201){
                error.append("返回信息="+rt.getResponseStr().substring(0, 200) + "...");
            }else{
                error.append("返回信息="+rt.getResponseStr());
            }
            WhyMessageQueue.pushKernelMessageLogQueue(null, LogPointCode.K03.getCode(), LogPointCode.K03.getMessage()  +"{"+desc+"}" + error);
        }
//        else{
//            log.warn("http请求成功（{}-{}-{}）：{}", requestCode, RequestCode.getMassageByCode(requestCode), desc, topeKernelProperties.getDomain() + uri);
//        }
        return rt;
    }

    /**
     * 获取集群内内核内网ip清单
     * */
    public Set<String> getAllServiceUrls() {
        HashSet<Member> memberSet = (HashSet<Member>) serverMemberManager.allMembers();
        HashSet<String> ipPortSet = memberSet.stream()
                                    .map(member -> member.getIp() + ":" + member.getPort())
                                    .collect(Collectors.toCollection(HashSet::new));
        return ipPortSet;
    }

    public static String getLocalServiceUrls() {
        ServerMemberManager serverMemberManager = (ServerMemberManager) WhySpringUtils.getApplicationContext().getBean("serverMemberManager");
        Member self = serverMemberManager.getSelf();
        return self.getIp()+separator+self.getPort();
    }


}
