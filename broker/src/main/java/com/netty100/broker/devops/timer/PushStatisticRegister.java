package com.netty100.broker.devops.timer;

import com.netty100.broker.autoconfig.WhyKernelProperties;
import com.netty100.broker.devops.entity.StatisticRegisterDto;
import com.netty100.broker.devops.utils.WhyCloudUtils;
import com.netty100.broker.utils.WhyCountUtils;
import com.netty100.common.http.RemotingHttpResult;
import com.netty100.common.protocol.RequestCode;
import com.netty100.common.utils.SysUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author why
 * @version 1.0.0, 2022/4/1
 * @since 1.0.0, 2022/4/1
 */
@Slf4j
@Component
public class PushStatisticRegister {
    @Autowired
    private WhyKernelProperties whyKernelProperties;

    //统计量上报
//    @Scheduled(cron = "45 * * * * ?")
    public void doCommand() {
        if(!whyKernelProperties.pushstatisticRegisterSwitch || SysUtility.isEmpty(whyKernelProperties.getDomain())){
            return;
        }

        statisticRegister();
    }

    public void statisticRegister() {
        StatisticRegisterDto dto = new StatisticRegisterDto();
        dto.setIntranetIp(SysUtility.getHostIp());
        dto.setPort(whyKernelProperties.getPort()+"");

        long platform_c2p_connect_active_total = WhyCountUtils.platform_c2p_connect_active_total.longValue();
        long platform_c2p_connect_inactive_total = WhyCountUtils.platform_c2p_connect_inactive_total.longValue();
        long platform_c2p_connect_error_total = WhyCountUtils.platform_c2p_connect_error_total.longValue();
        long platform_c2p_connect_idle_close_total = WhyCountUtils.platform_c2p_connect_idle_close_total.longValue();
        long platform_s2p_connect_active_total = WhyCountUtils.platform_s2p_connect_active_total.longValue();
        long platform_s2p_connect_inactive_total = WhyCountUtils.platform_s2p_connect_inactive_total.longValue();
        long platform_s2p_connect_error_total = WhyCountUtils.platform_s2p_connect_error_total.longValue();
        long platform_s2p_connect_idle_close_total = WhyCountUtils.platform_s2p_connect_idle_close_total.longValue();

        long platform_c2p_message_read_success_total = WhyCountUtils.platform_c2p_message_read_success_total.longValue();
        long platform_c2p_message_read_fail_total = WhyCountUtils.platform_c2p_message_read_fail_total.longValue();
        long platform_s2p_message_read_success_total = WhyCountUtils.platform_s2p_message_read_success_total.longValue();
        long platform_s2p_message_read_fail_total = WhyCountUtils.platform_s2p_message_read_fail_total.longValue();
        long platform_p2p_message_relay_total = WhyCountUtils.platform_p2p_message_relay_total.longValue();

        long platform_c2p_message_read_success_flow = WhyCountUtils.platform_c2p_message_read_success_flow.longValue();
        long platform_c2p_message_read_fail_flow = WhyCountUtils.platform_c2p_message_read_fail_flow.longValue();
        long platform_s2p_message_read_success_flow = WhyCountUtils.platform_s2p_message_read_success_flow.longValue();
        long platform_s2p_message_read_fail_flow = WhyCountUtils.platform_s2p_message_read_fail_flow.longValue();
        long platform_p2p_message_relay_flow = WhyCountUtils.platform_p2p_message_relay_flow.longValue();

        dto.setPlatformC2pConnectActiveTotal(platform_c2p_connect_active_total);
        dto.setPlatformC2pConnectInactiveTotal(platform_c2p_connect_inactive_total);
        dto.setPlatformC2pConnectErrorTotal(platform_c2p_connect_error_total);
        dto.setPlatformC2pConnectIdleCloseTotal(platform_c2p_connect_idle_close_total);
        dto.setPlatformS2pConnectActiveTotal(platform_s2p_connect_active_total);
        dto.setPlatformS2pConnectInactiveTotal(platform_s2p_connect_inactive_total);
        dto.setPlatformS2pConnectErrorTotal(platform_s2p_connect_error_total);
        dto.setPlatformS2pConnectIdleCloseTotal(platform_s2p_connect_idle_close_total);

        dto.setPlatformC2pMessageReadSuccessTotal(platform_c2p_message_read_success_total);
        dto.setPlatformC2pMessageReadFailTotal(platform_c2p_message_read_fail_total);
        dto.setPlatformS2pMessageReadSuccessTotal(platform_s2p_message_read_success_total);
        dto.setPlatformS2pMessageReadFailTotal(platform_s2p_message_read_fail_total);
        dto.setPlatformP2pMessageRelayTotal(platform_p2p_message_relay_total);

        dto.setPlatformC2pMessageReadSuccessFlow(platform_c2p_message_read_success_flow);
        dto.setPlatformC2pMessageReadFailFlow(platform_c2p_message_read_fail_flow);
        dto.setPlatformS2pMessageReadSuccessFlow(platform_s2p_message_read_success_flow);
        dto.setPlatformS2pMessageReadFailFlow(platform_s2p_message_read_fail_flow);
        dto.setPlatformP2pMessageRelayFlow(platform_p2p_message_relay_flow);
        //推送到一体化平台
        RemotingHttpResult rt = WhyCloudUtils.postBody(whyKernelProperties, RequestCode.Req07.getCode(), dto, 1);
        if(rt.getResponseCode() == 200){
            WhyCountUtils.platform_c2p_connect_active_total.add(-1 * platform_c2p_connect_active_total);
            WhyCountUtils.platform_c2p_connect_inactive_total.add(-1 * platform_c2p_connect_inactive_total);
            WhyCountUtils.platform_c2p_connect_error_total.add(-1 * platform_c2p_connect_error_total);
            WhyCountUtils.platform_c2p_connect_idle_close_total.add(-1 * platform_c2p_connect_idle_close_total);
            WhyCountUtils.platform_s2p_connect_active_total.add(-1 * platform_s2p_connect_active_total);
            WhyCountUtils.platform_s2p_connect_inactive_total.add(-1 * platform_s2p_connect_inactive_total);
            WhyCountUtils.platform_s2p_connect_error_total.add(-1 * platform_s2p_connect_error_total);
            WhyCountUtils.platform_s2p_connect_idle_close_total.add(-1 * platform_s2p_connect_idle_close_total);

            WhyCountUtils.platform_c2p_message_read_success_total.add(-1 * platform_c2p_message_read_success_total);
            WhyCountUtils.platform_c2p_message_read_fail_total.add(-1 * platform_c2p_message_read_fail_total);
            WhyCountUtils.platform_s2p_message_read_success_total.add(-1 * platform_s2p_message_read_success_total);
            WhyCountUtils.platform_s2p_message_read_fail_total.add(-1 * platform_s2p_message_read_fail_total);
            WhyCountUtils.platform_p2p_message_relay_total.add(-1 * platform_p2p_message_relay_total);

            WhyCountUtils.platform_c2p_message_read_success_flow.add(-1 * platform_c2p_message_read_success_flow);
            WhyCountUtils.platform_c2p_message_read_fail_flow.add(-1 * platform_c2p_message_read_fail_flow);
            WhyCountUtils.platform_s2p_message_read_success_flow.add(-1 * platform_s2p_message_read_success_flow);
            WhyCountUtils.platform_s2p_message_read_fail_flow.add(-1 * platform_s2p_message_read_fail_flow);
            WhyCountUtils.platform_p2p_message_relay_flow.add(-1 * platform_p2p_message_relay_flow);
        }
    }


}
