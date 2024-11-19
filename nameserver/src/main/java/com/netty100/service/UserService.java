package com.netty100.service;

import cn.hutool.crypto.digest.MD5;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.User;
import com.netty100.pojo.dto.UpdateUserDto;
import com.netty100.pojo.dto.UpdateUserDto0;
import com.netty100.pojo.dto.UserQueryDto;
import com.netty100.pojo.dto.UserRegisterDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author why
 */
@SuppressWarnings("ALL")
public interface UserService {


    MD5 md5 = new MD5("e19e5fb9fcd04223a186ab61cc42ac21".getBytes(StandardCharsets.UTF_8), 0, 5);

    static String digest(String arg) {
        return md5.digestHex(arg, StandardCharsets.UTF_8);
    }

    int updateLastLoginTimeById(Integer userId);

    int save(User user);

    PageInfo<User> getPage(UserQueryDto dto);

    int resetPassword(User user);

    int deleteById(Integer userId);

    User getById(Integer userId);

    User getByUsername(String username);

    int updateById(UpdateUserDto updateUserDto);

    List<User> getByIds(List<Integer> userIds);

    List<Integer> getAcceptUserIds();

    int updateById0(UpdateUserDto0 dto);

    void register(UserRegisterDto dto);

    List<User> getList();
}
