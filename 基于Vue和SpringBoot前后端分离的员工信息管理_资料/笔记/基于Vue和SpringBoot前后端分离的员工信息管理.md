# 库表设计

```SQL
CREATE TABLE t_user (
	id INT ( 6 ) PRIMARY KEY auto_increment,
	username VARCHAR ( 60 ),
	realname VARCHAR ( 60 ),
	PASSWORD VARCHAR ( 50 ),
	sex VARCHAR ( 4 ),
	STATUS VARCHAR ( 4 ),
	regsterTime TIMESTAMP 
);

CREATE TABLE t_emp (
	id INT ( 6 ) PRIMARY KEY auto_increment,
	NAME VARCHAR ( 40 ),
	path VARCHAR ( 100 ),
	salary DOUBLE ( 10, 2 ),
	age INT ( 3 ) 
);
```

# 搭建环境

# 验证码

`VerifyCodeUtils`

测试：`UserController`

```java
package com.honour.controller;

import com.honour.utils.VerifyCodeUtils;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author chenzufeng
 * @date 2021/06/07
 */

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {

    @GetMapping("getImage")
    public String getImageCode(HttpServletRequest request) throws IOException {
        // 1.使用工具类生成验证码
        String code = VerifyCodeUtils.generateVerifyCode(4);
        // 2.将验证码放入servletContext作用域
        request.getServletContext().setAttribute("code", code);
        // 3.将图片转为base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(120, 30, byteArrayOutputStream, code);
        return "data:image/png; base64, " + Base64Utils.encodeToString(byteArrayOutputStream.toByteArray());
    }
}
```

修改`regist.html`

测试地址：http://localhost:8080/ems_vue/regist.html



# 用户注册

`User`

```java
package com.honour.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author chenzufeng
 * @date 2021/06/07
 */

@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String username;
    private String realname;
    private String password;
    private String sex;
    private String status;
    private Date registerTime;
}
```

`UserDao`：注意使用`@Mapper`

```java
package com.honour.dao;

import com.honour.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chenzufeng
 * @date 2021/06/07
 * Mapper解决UserServiceImpl中自动注入userDao时出现
 *      “Could not autowire. No beans of 'UserDao' type found. ”
 */

@Mapper
public interface UserDao {
    /**
     * 保存用户
     * @param user 用户
     */
    void save(User user);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    User findByUserName(String username);
}
```

`UserDaoMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.honour.dao.UserDao">

    <!--save useGeneratedKeys自动生成id-->
    <insert id="save" parameterType="User" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO t_user VALUES (#{id}, #{username}, #{realname}, #{password}, #{sex}, #{status}, #{registerTime})
    </insert>

    <!--findByUserName-->
    <select id="findByUserName" parameterType="String" resultType="User">
        select id, username, realname, password, sex, status, registerTime
        from t_user where username = #{username}
    </select>

</mapper>
```

`UserService`

```java
package com.honour.service;

import com.honour.entity.User;

/**
 * @author chenzufeng
 * @date 2021/06/07
 */

public interface UserService {
    /**
     * 注册用户
     * @param user 用户
     */
    void register(User user);
}
```

`UserServiceImpl`

```java
package com.honour.service;

import com.honour.dao.UserDao;
import com.honour.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author chenzufeng
 * @date 2021/06/07
 */

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public void register(User user) {
        // 1.根据用户输入用户名判断用户是否存在
        User userTemp = userDao.findByUserName(user.getUsername());
        if (userTemp == null) {
            // 1.激活用户
            user.setStatus("已激活");
            // 2.设置用户注册时间
            user.setRegisterTime(new Date());
            // 3.保存用户
            userDao.save(user);
        } else {
            throw new RuntimeException("用户名已存在！");
        }
    }
}
```

`UserController`

```java
package com.honour.controller;

import com.honour.entity.User;
import com.honour.service.UserService;
import com.honour.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenzufeng
 * @date 2021/06/07
 */

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 生成验证码图片
     * @param request HttpServletRequest
     * @return String
     * @throws IOException
     */
    @GetMapping("getImage")
    public String getImageCode(HttpServletRequest request) throws IOException {
        // 1.使用工具类生成验证码
        String code = VerifyCodeUtils.generateVerifyCode(4);
        // 2.将验证码放入servletContext作用域
        request.getServletContext().setAttribute("code", code);
        // 3.将图片转为base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(120, 30, byteArrayOutputStream, code);
        return "data:image/png;base64," + Base64Utils.encodeToString(byteArrayOutputStream.toByteArray());
    }

    /**
     * 注册用户
     * @param user
     * @param code
     * @param request
     * @return
     *
     * RequestBody将json转换成对象
     */
    @PostMapping("register")
    public Map<String, Object> register(@RequestBody User user, String code, HttpServletRequest request) {
        log.info("用户信息：[{}]", user.toString());
        log.info("用户输入的验证码信息：[{}]", code);
        Map<String, Object> map = new HashMap<>();
        try {
            String key = (String) request.getServletContext().getAttribute("code");
            if (key.equalsIgnoreCase(code)) {
                // 验证码输入正确，调用业务方法
                userService.register(user);
                map.put("state", true);
                map.put("msg", "提示：注册成功！");
            } else {
                throw new RuntimeException("验证码输入错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", "提示：" + e.getMessage());
        }
        return map;
    }
}
```

# 用户登录

`UserService`

```java
package com.honour.service;

import com.honour.entity.User;

/**
 * @author chenzufeng
 * @date 2021/06/07
 */

public interface UserService {
    ...

    /**
     * 用户登录
     * @param user 用户
     * @return 用户
     */
    User login(User user);
}
```

`UserServiceImpl`

```java
package com.honour.service;

import com.honour.dao.UserDao;
import com.honour.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * @author chenzufeng
 * @date 2021/06/07
 */

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    ...

    @Override
    public User login(User user) {
        // 1.根据用户输入用户名进行查询
        User userTemp = userDao.findByUserName(user.getUsername());
        if (! ObjectUtils.isEmpty(userTemp)) {
            // 2.用户存在，继续比较密码
            if (userTemp.getPassword().equals(user.getPassword())) {
                return userTemp;
            } else {
                throw new RuntimeException("密码输入不正确！");
            }
        } else {
            throw new RuntimeException("用户名输入错误！");
        }
    }
}
```

`UserController`

```java
package com.honour.controller;

import com.honour.entity.User;
import com.honour.service.UserService;
import com.honour.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenzufeng
 * @date 2021/06/07
 */

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /...

    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("login")
    public Map<String, Object> login(@RequestBody User user) {
        log.info("当前登录用户的信息：[{}]", user.toString());
        Map<String, Object> map = new HashMap<>();
        try {
            User userDataBase =  userService.login(user);
            map.put("state", true);
            map.put("msg", "登录成功！");
            map.put("user", userDataBase);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}
```

# 用户信息展示

将登录成功的信息响应给前端，并将该信息存入LocalStorage中
