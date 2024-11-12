package com.netty100.kernel.devops.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/4/11
 * @since 1.0.0, 2022/4/11
 */
@Data
public class StatisticRegisterDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String intranetIp;

    private String port;

    private Long platformC2pConnectActiveTotal;

    private Long platformC2pConnectInactiveTotal;

    private Long platformC2pConnectErrorTotal;

    private Long platformC2pConnectIdleCloseTotal;

    private Long platformS2pConnectActiveTotal;

    private Long platformS2pConnectInactiveTotal;

    private Long platformS2pConnectErrorTotal;

    private Long platformS2pConnectIdleCloseTotal;

    private Long platformC2pMessageReadSuccessTotal;

    private Long platformC2pMessageReadFailTotal;

    private Long platformS2pMessageReadSuccessTotal;

    private Long platformS2pMessageReadFailTotal;

    private Long platformP2pMessageRelayTotal;

    private Long platformC2pMessageReadSuccessFlow;

    private Long platformC2pMessageReadFailFlow;

    private Long platformS2pMessageReadSuccessFlow;

    private Long platformS2pMessageReadFailFlow;

    private Long platformP2pMessageRelayFlow;


}
