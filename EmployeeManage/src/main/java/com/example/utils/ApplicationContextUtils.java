package com.example.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 用来获取SpringBoot创建好的工厂
 * 便于在Redis中注入RedisTemplate
 * @author chenzufeng
 * @date 2021-07-04
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    /**
     * 获取到的SpringBoot创建好的工厂
     */
    private static ApplicationContext applicationContext;

    /**
     * 将创建好的工厂，以参数形式传递给这个类
     * @param applicationContext SpringBoot创建好的工厂
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }

    /**
     * 提供在工厂中获取的对象的方法
     * @param beanName 对象名字 StringRedisTemplate、RedisTemplate
     * @return 对象
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
}
