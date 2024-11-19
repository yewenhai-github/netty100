package com.netty100.service.impl;

import com.netty100.entity.User;
import com.netty100.pojo.dto.LoginDto;
import com.netty100.service.LoginService;
import com.netty100.service.UserService;
import com.netty100.utils.Jwts;
import com.netty100.utils.exception.CommonException;
import com.netty100.utils.respons.ResponseMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author why
 */
@Service
public class LoginServiceImpl implements LoginService {

    private UserService userService;

    private Jwts jwts;

    @Override
    public Map<String, Object> login(LoginDto dto) {
        User user = userService.getByUsername(dto.getUsername());
        if (Objects.isNull(user)) {
            throw new CommonException(ResponseMsg.USER_NOT_EXIST);
        }
        String password = UserService.digest(dto.getPassword());
        if (!user.getPassword().contentEquals(password)) {
            throw new CommonException(ResponseMsg.INCORRECT_USERNAME_PASSWORD);
        }
        String token = jwts.create(user);
        if (Objects.isNull(token)) {
            throw new CommonException(ResponseMsg.JWT_CREAT_ERROR);
        }
        userService.updateLastLoginTimeById(user.getId());
        Map<String, Object> map = new HashMap<>(4);
        map.put("token", token);
        map.put("user", user);
        return map;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setJwts(Jwts jwts) {
        this.jwts = jwts;
    }
}
