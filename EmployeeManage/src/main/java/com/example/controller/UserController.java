package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.VerifyCodeUtils;
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
 * @date 2021-07-01
 * CrossOrigin：允许跨域
 */
@RestController
@CrossOrigin
@RequestMapping("/User")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 生成验证码
     * @param request request
     * @return 验证码
     * @throws IOException IOException
     */
    @GetMapping("/GetVerifyCode")
    public String getVerifyCode(HttpServletRequest request) throws IOException {
        // 使用工具类生成验证码
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        // 将验证码放入servletContext作用域
        request.getServletContext().setAttribute("verifyCode", verifyCode);
        // 将图片转为base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(120, 40, byteArrayOutputStream, verifyCode);
        // 前端img标签显示base64格式的图片：data:image/png;base64,base64编码的png图片数据
        return "data:image/png;base64," + Base64Utils.encodeToString(byteArrayOutputStream.toByteArray());
    }

    /**
     * 用户注册
     * @param user 用户
     * @param inputVerifyCode 用户输入的验证码
     * @param request request
     * @return Map含注册状态信息
     *
     * 由于axios是以Json字符串形式在传数据，使用RequestBody将Json数据转换成对象
     */
    @PostMapping("UserRegister")
    public Map<String, Object> registerUser(@RequestBody User user, String inputVerifyCode, HttpServletRequest request) {
        log.info("用户信息：{}", user.toString());
        log.info("用户输入的验证码：{}", inputVerifyCode);

        Map<String, Object> map = new HashMap<>();
        try {
            /*
             * 这里"verifyCode"要与
             * getVerifyCode#request.getServletContext().setAttribute("verifyCode", verifyCode)
             * 中"verifyCode"一致
             */
            String verifyCode = (String) request.getServletContext().getAttribute("verifyCode");
            if (verifyCode.equalsIgnoreCase(inputVerifyCode)) {
                // 调用业务方法
                userService.register(user);
                map.put("UserState", true);
                map.put("message", "提示：注册成功！");
            } else {
                throw new RuntimeException("验证码输入错误！");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("UserState", false);
            map.put("message", "提示：" + exception.getMessage());
        }
        return map;
    }

    /**
     * 用户登录：填入的用户在用户表中，为成功
     * @param user 用户
     * @return 登录状态信息
     */
    @PostMapping("UserLogin")
    public Map<String, Object> login(@RequestBody User user) {
        log.info("当前登录用户的信息：{}", user.toString());
        Map<String, Object> map = new HashMap<>();
        try {
            User existUser = userService.login(user);
            map.put("state", true);
            map.put("message", "登录成功！");
            map.put("User", existUser);
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("state", false);
            map.put("message", exception.getMessage());
        }
        return map;
    }
}
