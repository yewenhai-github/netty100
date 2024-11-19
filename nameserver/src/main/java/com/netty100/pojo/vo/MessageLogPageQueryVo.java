package com.netty100.pojo.vo;

import com.netty100.entity.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author why
 */
@Getter
@Setter
public class MessageLogPageQueryVo {

    private Long count;

    private List<List<Message>> result;
}

