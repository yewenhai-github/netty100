package com.netty100.mapper;

import com.netty100.entity.User;
import com.netty100.pojo.dto.UpdateUserDto;
import com.netty100.pojo.dto.UpdateUserDto0;
import com.netty100.pojo.dto.UserQueryDto;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author why
 * @since 2022-03-21
 */
public interface UserMapper {


    int updateLastLoginTimeById(Integer userId);

    int save(User user);

    List<User> getList(UserQueryDto dto);

    int resetPassword(User user);

    int deleteById(Integer userId);

    User getById(Integer userId);

    User getByUsername(String username);

    int updateById(UpdateUserDto updateUserDto);

    List<User> getByIds(List<Integer> userIds);

    List<Integer> getAcceptUserIds();

    int updateById0(UpdateUserDto0 dto);

    List<User> getAll();

}
