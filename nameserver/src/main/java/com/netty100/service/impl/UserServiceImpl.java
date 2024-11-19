package com.netty100.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netty100.entity.User;
import com.netty100.enumeration.AcceptWarn;
import com.netty100.enumeration.UserType;
import com.netty100.mapper.UserMapper;
import com.netty100.pojo.dto.UpdateUserDto;
import com.netty100.pojo.dto.UpdateUserDto0;
import com.netty100.pojo.dto.UserQueryDto;
import com.netty100.pojo.dto.UserRegisterDto;
import com.netty100.service.UserClusterService;
import com.netty100.service.UserService;
import com.netty100.utils.exception.CommonException;
import com.netty100.utils.respons.ResponseMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author why
 * @since 2019-09-18
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private UserClusterService userClusterService;

    @Autowired
    public void setUserClusterService(UserClusterService userClusterService) {
        this.userClusterService = userClusterService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLastLoginTimeById(Integer userId) {
        return userMapper.updateLastLoginTimeById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(User user) {
        return userMapper.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<User> getPage(UserQueryDto dto) {
        if (StringUtils.hasText(dto.getOrderBy())) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize(), dto.getOrderBy() + " " + dto.getOrderFlag());
        } else {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.getList(dto));
        pageInfo.getList().forEach(user -> {
            user.setUserTypeDesc(user.getUserType() == UserType.USER ? "普通用户" : "管理员");
            user.setAcceptWarnDesc(user.getAcceptWarn() == AcceptWarn.YES ? "是" : "否");
        });
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resetPassword(User user) {
        return userMapper.resetPassword(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Integer userId) {
        userClusterService.deleteUser(userId);
        return userMapper.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Integer userId) {
        return userMapper.getById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateById(UpdateUserDto updateUserDto) {
        return userMapper.updateById(updateUserDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getByIds(List<Integer> userIds) {
        return userMapper.getByIds(userIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAcceptUserIds() {
        return userMapper.getAcceptUserIds();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateById0(UpdateUserDto0 dto) {
        return userMapper.updateById0(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDto dto) {
        //如果设置为接收告警,钉钉手机号码和邮箱不能同时为空
        if (Objects.equals(dto.getAcceptWarn(), 1) && !StringUtils.hasText(dto.getEmail()) && !StringUtils.hasText(dto.getDingTalk())) {
            throw new CommonException(ResponseMsg.INVALID_EMAIL_DING_TALK);
        }
        //只有管理员才能接收告警信息
        if (Objects.equals(dto.getAcceptWarn(), 1) && Objects.equals(dto.getUserType(), 0)) {
            throw new CommonException(ResponseMsg.ACCEPT_WARN_STATUS);
        }
        //username不能相同
        User user = this.getByUsername(dto.getUsername());
        if (Objects.nonNull(user)) {
            throw new CommonException(ResponseMsg.USERNAME_ALREADY_EXISTS);
        }
        user = new User();
        String password = UserService.digest(dto.getPassword());
        user.setUsername(dto.getUsername());
        user.setPassword(password);
        user.setUserType(dto.getUserType());
        user.setDingTalk(dto.getDingTalk());
        user.setEmail(dto.getEmail());
        user.setAcceptWarn(dto.getAcceptWarn());
        this.save(user);
    }

    @Override
    public List<User> getList() {
        return userMapper.getAll();
    }
}
