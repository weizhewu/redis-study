package com.example.redisstudy.service;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: Redisson
 * @createDate: 2023/5/18 9:50
 **/
public interface RedissonService {
    /**
     * 获取公平锁：先到先得
     */
    void getFairLock();

    /**
     * 获取联锁：只有所有资源都加锁成功的时候，联锁才会成功
     */
    void getRedissonMultiLock();

    /**
     * 获取红锁：在过半的资源都加锁成功的时候，红锁才会成功
     */
    void getRedissonRedLock();

    /**
     * 获取可重入锁：同一个线程可以多次获取该锁，而不会出现死锁
     */
    void getLock();

    /**
     * 获取读写锁：允许同时有多个读取锁，但是最多只能有一个写入锁
     */
    void getReadWriteLock();

    /**
     * 获取闭锁：等待其他线程各自执行完毕后再执行
     */
    void getCountDownLatch();

    /**
     * 信号量操作
     */
    void operateSemaphore();

    /**
     * 获取可过期性信号量
     */
    void getPermitExpirableSemaphore();
}
