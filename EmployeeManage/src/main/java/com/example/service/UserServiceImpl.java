package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * @author chenzufeng
 * @date 2021-07-01
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public void register(User user) {
        // 根据用户输入的名字判断用户是否已注册
        User userFoundByName = userDao.findUserByName(user.getName());
        if (userFoundByName == null) {
            // 设置用户状态
            user.setStatus("已激活");
            // 设置用户创建时间
            user.setRegisterTime(new Date());
            // 注册用户
            userDao.saveUser(user);
        } else {
            throw new RuntimeException("用户已存在！");
        }
    }

    /**
     * 用户登录
     * @param user 用户
     * @return 用户
     */
    @Override
    public User login(User user) {
        // 根据用户输入的用户名进行查询
        User userFoundByName = userDao.findUserByName(user.getName());
        // 输入的用户存在
        if (! ObjectUtils.isEmpty(userFoundByName)) {
            // 验证密码是否正确
            if (userFoundByName.getPassword().equals(user.getPassword())) {
                return userFoundByName;
            } else {
                throw new RuntimeException("密码输入错误！");
            }
        } else {
            throw new RuntimeException("输入的用户不存在，请重新输入或进行注册！");
        }
    }
}
