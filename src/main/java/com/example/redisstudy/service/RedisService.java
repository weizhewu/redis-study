package com.example.redisstudy.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public RedisService() {
    }

    /**
     * 新增缓存
     * @param key key
     * @param object value
     * @param outTime 过期时间 单位：秒
     */
    public void saveObject(String key, Object object, long outTime) {
        this.redisTemplate.opsForValue().set(key, object, outTime, TimeUnit.SECONDS);
    }

    /**
     * 新增缓存
     * @param key key
     * @param object value
     */
    public void saveObject(String key, Object object) {
        this.redisTemplate.opsForValue().set(key, object);
    }

    /**
     * 获取缓存
     * @param key key
     * @return 缓存
     */
    public Object getObject(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    public String getObjectString(String key){
        return (String) this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 根据key移除缓存
     * @param key key
     */
    public void removeKey(String key) {
        this.redisTemplate.delete(key);
    }

    /***
     * 根据key判断缓存是否存在
     * @param key key
     * @return ture：存在
     */
    public boolean existsKey(String key) {
        return Boolean.TRUE.equals(this.redisTemplate.hasKey(key));
    }

    /**
     * 根据key设置缓存过期时间
     * @param key key
     * @param outTime 过期时间 单位：秒
     * @return 设置成功
     */
    public boolean expire(String key, int outTime) {
        this.redisTemplate.expire(key, (long)outTime, TimeUnit.SECONDS);
        return true;
    }
    /**
     * 根据key将储存的数字值增1
     * @param key key
     * @return 递增的值
     */
    public Long increment(String key) {
        return this.redisTemplate.opsForValue().increment(key, 1L);
    }

    /**
     * 根据key删除缓存
     * @param key key
     */
    public void existAndDelKey(String key) {
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(key))) {
            this.redisTemplate.delete(key);
        }

    }

    /**
     * 根据key清除所有缓存
     * @param key key
     */
    @Deprecated
    public void flushAllKeys(String key) {
        // key+"*"
        Set<String> keys = this.redisTemplate.keys(key + "*");
        if (Objects.nonNull(keys)) {
            this.redisTemplate.delete(keys);
        }
    }

    /**
     * 如果key不存在则新增，不存在则不做任何操作
     * @param key key
     * @param value value
     * @param sec 过期时间 单位：秒
     * @return 是否新增
     */
    public boolean setIfAbsent(String key, Object value, long sec) {
        Boolean aBoolean = this.redisTemplate.opsForValue().setIfAbsent(key, value, sec, TimeUnit.SECONDS);
        return null != aBoolean && aBoolean;
    }

    /**
     * 获取RedisTemplate
     * @return RedisTemplate
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

    /**
     * 根据key获取对应的缓存Map，再获取Map中item对应的值
     * @param key key
     * @param mapKey map中的key
     * @return Map中item对应的值
     */
    public Object getMapValue(String key, String mapKey) {
        return this.redisTemplate.opsForHash().get(key, mapKey);
    }

    /**
     * 根据key获取对应的缓存Map
     * @param key key
     * @return Map<Object, Object>
     */
    public Map<Object, Object> getMap(String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    /**
     * 根据key获取对应的缓存Map中所有的值
     * @param key key
     * @return List<Object>
     */
    public List<Object> getMapValues(String key) {
        return this.redisTemplate.opsForHash().values(key);
    }

    /**
     * 根据key获取对应的缓存Map中所有的键
     * @param key key
     * @return Set<Object>
     */
    public Set<Object> getMapKeys(String key) {
        return this.redisTemplate.opsForHash().keys(key);
    }

    /**
     * 直接以map集合的方式添加key对应的值
     * map中的key已经存在，覆盖替换
     * map中的key不存在，新增
     * @param key key
     * @param map map
     * @return true：添加成功 false：添加失败
     * @param <T> 泛型
     */
    public <T> boolean putMap(String key, Map<String, T> map) {
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }
    }

    /**
     * 直接以map集合的方式添加key对应的值，并设置过期时间
     * map中的key已经存在，覆盖替换
     * map中的key不存在，新增
     * @param key key
     * @param map map
     * @param time 过期时间，单位秒
     * @return true：添加成功 false：添加失败
     * @param <T> 泛型
     */
    public <T> boolean putMap(String key, Map<String, T> map, long time) {
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            if (time > 0L) {
                this.expire(key, time);
            }

            return true;
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key获取对应的map，并设置item对应的值，若存在则覆盖，不存在则新增
     * @param key key
     * @param mapKey map的key
     * @param value map的value
     * @return true：添加成功 false：添加失败
     */
    public boolean setMapValue(String key, String mapKey, Object value) {
        try {
            this.redisTemplate.opsForHash().put(key, mapKey, value);
            return true;
        } catch (Exception var5) {
            var5.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key获取对应的map，并设置item对应的值，若存在则覆盖，不存在则新增，并且设置过期时间
     * @param key key
     * @param mapKey map的key
     * @param value map的value
     * @param time 过期时间，单位秒
     * @return true：添加成功 false：添加失败
     */
    public boolean setMapValue(String key, String mapKey, Object value, long time) {
        try {
            this.redisTemplate.opsForHash().put(key, mapKey, value);
            if (time > 0L) {
                this.expire(key, time);
            }

            return true;
        } catch (Exception var7) {
            var7.printStackTrace();
            return false;
        }
    }

    /**
     * 删除key对应map中item对应的值
     * @param key key
     * @param mapKey map的key
     */
    public void deleteMapValue(String key, Object... mapKey) {
        this.redisTemplate.opsForHash().delete(key, mapKey);
    }

    /**
     * 删除key对应map
     * @param key key
     */
    public void deleteMap(String key) {
        this.redisTemplate.opsForHash().delete(key);
    }

    /**
     * 判断key对应Map中是否存在键为item
     * @param key key
     * @param mapKey map对应的key
     * @return true：存在 false：不存在
     */
    public boolean ifMapHasKey(String key, String mapKey) {
        return this.redisTemplate.opsForHash().hasKey(key, mapKey);
    }

    /**
     * key对应Map中mapKey对应的值自增value
     * @param key key
     * @param mapKey map对应的key
     * @param value 要自增的值
     * @return 自增的值
     */
    public double incrementMapValue(String key, String mapKey, double value) {
        return this.redisTemplate.opsForHash().increment(key, mapKey, value);
    }

    /**
     * key对应Map中mapKey对应的值自增value
     * @param key key
     * @param mapKey map对应的key
     * @param value 要自增的值
     * @return 自增的值
     */
    public double decrementMapValue(String key, String mapKey, double value) {
        return this.redisTemplate.opsForHash().increment(key, mapKey, -value);
    }

    /**
     * 给key对应缓存设置过期时间
     * @param key key
     * @param time 过期时间，单位：秒
     * @return ture；新增成功 false：新增失败
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0L) {
                this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }

            return true;
        } catch (Exception var5) {
            var5.printStackTrace();
            return false;
        }
    }

    /**
     *
     * 给key对应缓存设置过期时间
     * @param key key
     * @param time 过期时间，单位：秒
     * @param timeUnit TimeUnit
     * @return ture；新增成功 false：新增失败
     */
    public boolean expire(String key, long time,TimeUnit timeUnit) {
        try {
            if (time > 0L) {
                this.redisTemplate.expire(key, time,timeUnit);
            }

            return true;
        } catch (Exception var5) {
            var5.printStackTrace();
            return false;
        }
    }
}
