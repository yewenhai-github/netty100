package com.netty100.controller.app;

import com.netty100.pojo.dto.MessageQueryDto;
import com.netty100.pojo.vo.MessageLogPageQueryVo;
import com.netty100.service.MessageService;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * @author why
 */
@Validated
@RestController
@RequestMapping(value = "/app/message")
@Api(tags = "【页面功能-用户端-消息查询】")
public class MessageLogController {

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @ApiOperation(value = "消息分页查询", notes = "游戏端发送消息分页查询")
    @PostMapping(value = "/page")
    public WebResult<MessageLogPageQueryVo> page(@Valid @RequestBody MessageQueryDto dto) {
        return WebResult.ok(messageService.page(dto));
    }
}
