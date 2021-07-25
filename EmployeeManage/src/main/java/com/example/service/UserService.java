package com.example.service;

import com.example.entity.User;

/**
 * @author chenzufeng
 * @date 2021-07-01
 */
public interface UserService {
    /**
     * 用户注册
     * @param user 用户
     */
    void register(User user);

    /**
     * 用户登录
     * @param user 用户
     * @return 用户信息
     */
    User login(User user);
}