package com.example.redisstudy.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: redis测试
 * @createDate: 2023/4/11 15:00
 **/
@SpringBootTest
class RedisServiceTest {
    @Resource
    private RedisService redisService;

    @Test
    void saveObject() {
        redisService.saveObject("key1","value1");
    }

    @Test
    void getObject() {
        System.out.println(redisService.getObject("key1"));
    }
}