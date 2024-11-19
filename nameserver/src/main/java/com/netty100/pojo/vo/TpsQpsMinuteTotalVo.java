package com.netty100.pojo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author why
 */
@Getter
@Setter
public class TpsQpsMinuteTotalVo {

    private Integer tpsTotal;

    private Integer qpsTotal;

    private String createTime;
}
