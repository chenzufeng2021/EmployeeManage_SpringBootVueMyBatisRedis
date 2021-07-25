package com.example.cache;

import com.example.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 自定义RedisCache
 * import org.apache.ibatis.cache.Cache
 * Cache由MyBatis进行实例化，Spring无法获取工厂对象
 * 因此需要实现ApplicationContextUtils从而可以注入RedisTemplate
 * @author chenzufeng
 * @date 2021-07-04
 */
@Slf4j
public class RedisCache implements Cache {
    private String id;

    /**
     * Base cache implementations must have a constructor
     * that takes a String id as a parameter
     * @param id EmployeeDaoMapper.xml中的namespace：com.example.dao.EmployeeDao
     */
    public RedisCache(String id) {
        log.info("当前缓存id：{}", id);
        this.id = id;
    }

    /**
     * 获取Cache唯一标识
     * 为了根据namespace进行分类，将同一个namespace下的缓存放在一起
     *
     * @return 放入缓存的EmployeeDaoMapper.xml中的namespace
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * 放入Redis缓存
     * 如果不存在，则直接去数据库中查询
     * @param key key
     * @param value value
     */
    @Override
    public void putObject(Object key, Object value) {
        log.info("放入的缓存key：[{}]和value：[{}]", key, value);
        getRedisTemplate().opsForHash().put(id.toString(), key.toString(), value);
    }

    /**
     * 获取缓存
     * @param key 根据key从redis的hash类型中获取数据
     * @return 获取的对象
     */
    @Override
    public Object getObject(Object key) {
        log.info("获取缓存的Key：[{}]", key.toString());
        return getRedisTemplate().opsForHash().get(id.toString(), key.toString());
    }

    @Override
    public Object removeObject(Object key) {
        return null;
    }

    @Override
    public void clear() {
        log.info("清除所有缓存信息..............");
        getRedisTemplate().delete(id.toString());
    }

    @Override
    public int getSize() {
        /*
         * 获取hash中key和value对的数量；
         * size()返回的是long类型数据，需对其进行转型
         */
        return getRedisTemplate().opsForHash().size(id.toString()).intValue();
    }

    /**
     * 封装RedisTemplate
     * @return RedisTemplate
     */
    private RedisTemplate getRedisTemplate() {
        // 无法通过注入获取RedisTemplate，因此通过工具类获取
        RedisTemplate redisTemplate =
                (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        // 设置Key序列化策略为字符串序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
