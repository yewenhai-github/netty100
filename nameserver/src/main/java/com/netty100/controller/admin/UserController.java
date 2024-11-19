package com.netty100.controller.admin;


import com.github.pagehelper.PageInfo;
import com.netty100.entity.User;
import com.netty100.pojo.dto.UpdateUserDto;
import com.netty100.pojo.dto.UserQueryDto;
import com.netty100.pojo.dto.UserRegisterDto;
import com.netty100.service.UserService;
import com.netty100.utils.WebResult;
import com.netty100.utils.respons.ResponseMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
@SuppressWarnings("DuplicatedCode")
@Validated
@RestController(value = "admin-user")
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Api(tags = "【页面功能-管理端-用户管理】")
public class UserController {

    private UserService userService;

    /**
     * 用户注册
     */
    @ApiOperation(value = "注册")
    @PostMapping(value = "/register")
    public WebResult<?> save(@Validated @RequestBody UserRegisterDto dto) {
        userService.register(dto);
        return WebResult.ok();
    }

    /**
     * 分页获取用户,查询条件只支持用户名称模糊查询,用户类型
     */
    @ApiOperation(value = "分页查询")
    @PostMapping(value = "/page")
    public WebResult<PageInfo<User>> getPage(@RequestBody UserQueryDto dto) {
        addFuzzyExpression(dto);
        return WebResult.ok(userService.getPage(dto));
    }

    /**
     * 管理员更新用户信息,允许更新用户类型,邮箱,钉钉手机号码,是否接收告警状态信息
     */
    @ApiOperation(value = "更新")
    @PostMapping(value = "/update")
    public WebResult<?> update(@Validated @RequestBody UpdateUserDto dto) {
        if (Objects.equals(dto.getAcceptWarn(), 1) && !StringUtils.hasText(dto.getEmail()) && !StringUtils.hasText(dto.getDingTalk())) {
            return WebResult.error(ResponseMsg.INVALID_EMAIL_DING_TALK);
        }
        if (Objects.equals(dto.getAcceptWarn(), 1) && Objects.equals(dto.getUserType(), 0)) {
            return WebResult.error(ResponseMsg.ACCEPT_WARN_STATUS);
        }
        userService.updateById(dto);
        return WebResult.ok();
    }

    @ApiOperation(value = "删除")
    @PostMapping(value = "/delete/{id}")
    public WebResult<?> delete(@PathVariable(value = "id") Integer id) {
        userService.deleteById(id);
        return WebResult.ok();
    }

    @PostMapping(value = "/list")
    @ApiOperation(value = "获取用户列表")
    public WebResult<List<User>> getList(){
        List<User> users = userService.getList();
        return WebResult.ok(users);
    }

    private void addFuzzyExpression(UserQueryDto dto) {
        if (StringUtils.hasText(dto.getUsername())) {
            String username = dto.getUsername();
            username = "%" + username.trim() + "%";
            dto.setUsername(username);
        }
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
