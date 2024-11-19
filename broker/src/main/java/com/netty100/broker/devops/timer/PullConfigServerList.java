package com.netty100.broker.devops.timer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netty100.cluster.naming.core.v2.client.impl.IpPortBasedClient;
import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.broker.protocol.TimerRunConstants;
import com.netty100.common.http.RemotingHttpResult;
import com.netty100.common.protocol.RequestCode;
import com.netty100.common.utils.SysUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * @author why
 * @version 1.0.0, 2022/6/8
 * @since 1.0.0, 2022/6/8
 */
@Slf4j
@Component
public class PullConfigServerList {
    @Autowired
    private WhyKernelProperties whyKernelProperties;

    public static final String kernelStr = "0:0:1";

//    @Scheduled(cron = "45 * * * * ?")
    public void doCommand() {
        doCommand(whyKernelProperties);
    }

    public void doCommand(WhyKernelProperties whyKernelProperties){
        if(SysUtility.isEmpty(whyKernelProperties.getDomain())){
            return;
        }
        try {
            RemotingHttpResult rt = WhyCloudUtils.postBody(whyKernelProperties, RequestCode.Req24.getCode(), new JSONObject(), 1);
            if(TimerRunConstants.HTTP_CODE_SUCCESS == rt.getResponseCode()){
                JSONObject jsonObject = JSONObject.parseObject(rt.getResponseStr());
                JSONArray datas = (JSONArray)jsonObject.get("data");
                if(SysUtility.isNotEmpty(datas)){
                    for (int i = 0; i < datas.size(); i++) {
                        JSONObject data = (JSONObject)datas.get(i);
                        WhyCloudUtils.serviceConfigs.add(data.getString("messageSource")+ IpPortBasedClient.ID_DELIMITER+data.getString("messageDest"));
                    }
                    WhyCloudUtils.serviceConfigs.stream().sorted(Comparator.naturalOrder());
                }
            }else{
                log.error("系统接入配置更新失败，请求结果{}", rt.getResponseStr());
            }
        } catch (Exception e) {
            log.error("异常错误{}", e);
        } finally {
            //兜底方案，默认本机
            if(!WhyCloudUtils.serviceConfigs.contains(kernelStr)){
                WhyCloudUtils.serviceConfigs.add(kernelStr);
            }
        }
    }



}
