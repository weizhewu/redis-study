package com.example.redisstudy.constant;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: redis常量类
 * @createDate: 2023/4/6 14:29
 **/
public class RedisConstant {
    /**
     * TOKEN前缀
     */
    String REDIS_PREFIX_LOGIN = "login_token_%s";

    /**
     * redis-OK
     */
    public static final String OK = "OK";

    /**
     * 暂无过期时间
     */
    Integer REDIS_EXPIRE_NULL = -1;

    /**
     * redis过期时间，一秒钟
     */
    public static final int EXPR_SECOND = 1000;

    /**
     * redis过期时间，一分钟
     */
    public static final int EXPR_MINUTE = EXPR_SECOND * 60;

    /**
     * redis过期时间，一小时
     */
    public static final int EXPR_HOUR = EXPR_MINUTE * 60;

    /**
     * redis过期时间，一天
     */
    public static final int EXPR_DAY = EXPR_HOUR * 24;

    /**
     * redis-key-前缀:menu:shiro:cache:
     */
    public static final String PREFIX_SHIRO_CACHE = "menu:shiro:cache:";

    /**
     * redis-key-前缀:menu:shiro:refresh_token:
     */
    public static final String PREFIX_SHIRO_REFRESH_TOKEN = "menu:shiro:refresh_token:";

    /**
     * refreshToken过期时间,，三十分钟
     */
    public final static int REFRESH_TOKEN_EXPIRATION_TIME = EXPR_MINUTE * 30;

    /**
     * shiroCacheExpireTime过期时间，1小时
     */
    public final static int SHIRO_CACHE_EXPIRATION_TIME = EXPR_HOUR;

    /**
     * redis-key前缀：shiro_redis_key_
     */
    public static final String SHIRO_REDIS_KEY = "shiro_redis_key_";

    /**
     * redis-key后缀：_lockCount
     */
    public static final String LOCK_COUNT_TYPE = "_lockCount";

    /**
     * redis-key后缀：_disableCount
     */
    public static final String DISABLE_TYPE = "_disableCount";




}
