package com.netty100.controller.app;

import com.netty100.entity.User;
import com.netty100.pojo.dto.LoginDto;
import com.netty100.pojo.dto.ResetPasswordDto;
import com.netty100.pojo.dto.UpdateUserDto0;
import com.netty100.service.LoginService;
import com.netty100.service.UserService;
import com.netty100.utils.SecurityContext;
import com.netty100.utils.WebResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author why
 */
@Validated
@RestController(value = "app-user")
@RequestMapping(value = "/app/user")
@Api(tags = "【页面功能-用户端-登录相关】")
public class UserController {

    private LoginService loginService;

    private UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public WebResult<Map<String, Object>> login(@Validated @RequestBody LoginDto dto) {
        return WebResult.ok(loginService.login(dto));
    }

    /**
     * 重置密码需要在登录状态完成,重置密码完成之后需要重新登录
     */
    @ApiOperation(value = "密码重置")
    @PostMapping(value = "/password")
    public WebResult<?> resetPassword(@Validated @RequestBody ResetPasswordDto dto) {
        final User user = SecurityContext.getUser();
        String password = UserService.digest(dto.getNewPassword());
        user.setPassword(password);
        userService.resetPassword(user);
        return WebResult.ok();
    }

    /**
     * 普通用户自己更新,允许更新邮箱,钉钉手机号码
     */
    @ApiOperation(value = "信息更新", notes = "用户更新自身信息")
    @PostMapping(value = "/update")
    public WebResult<?> update(@Validated @RequestBody UpdateUserDto0 dto) {
        userService.updateById0(dto);
        return WebResult.ok();
    }

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
