package com.netty100.service;

import com.netty100.pojo.dto.LoginDto;

import java.util.Map;

/**
 * @author why
 */
@SuppressWarnings("all")
public interface LoginService {

    Map<String, Object> login(LoginDto dto);
}
