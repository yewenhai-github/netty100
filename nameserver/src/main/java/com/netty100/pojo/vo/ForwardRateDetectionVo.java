package com.netty100.pojo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
public class ForwardRateDetectionVo {

    private Integer forwardTimes;

    private Integer c2pTotalTimes;

    private Integer s2pTotalTimes;
}
