package com.example.dao;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chenzufeng
 * @date 2021-07-01
 * Mapper用来创建UserDao对象，用于在UserService注入
 */
@Mapper
public interface UserDao {
    /**
     * 用户注册
     * @param user 用户
     */
    void saveUser(User user);

    /**
     * 根据用户名查找用户
     * @param name 用户名
     * @return User
     */
    User findUserByName(String name);
}
