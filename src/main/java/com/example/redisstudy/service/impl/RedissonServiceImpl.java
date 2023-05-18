package com.example.redisstudy.service.impl;

import com.example.redisstudy.service.RedissonService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: Redisson
 * @createDate: 2023/5/18 9:51
 **/
@Service
@Slf4j
public class RedissonServiceImpl implements RedissonService {
    @Resource
    private RedissonClient redissonClient;
    @Override
    public void getFairLock() {
        RLock fairLock = redissonClient.getFairLock("锁的名称");
        try {
            // 最多等待10毫秒，获取锁8毫秒后自动释放
            boolean lockFlag = fairLock.tryLock(10L, 8L, TimeUnit.SECONDS);
            if (lockFlag) {
                // 获得锁，执行一些操作
            }
        } catch (Exception e) {
            log.error("发生异常,详细信息：",e);

        } finally {
            // 如果锁仍被当前线程持有，就释放锁
            if (fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
            }
        }
    }

    @Override
    public void getRedissonMultiLock() {
        // 也可以是其他类型锁
        RLock lock1 = redissonClient.getFairLock("lock1");
        RLock lock2 = redissonClient.getFairLock("lock2");
        RLock lock3 = redissonClient.getFairLock("lock3");
        // 同时加锁：lock1 lock2 lock3
        RedissonMultiLock multiLock = new RedissonMultiLock(lock1,lock2,lock3);
        try {
            // 所有的锁都加上了才算成功
            // 最多等待10毫秒，获取锁8毫秒后自动释放
            boolean lockFlag = multiLock.tryLock(10L, 8L, TimeUnit.SECONDS);
            if (lockFlag) {
                // 获得锁，执行一些操作
            }
        } catch (Exception e){
            log.error("发生异常,详细信息：",e);
        } finally {
            // 如果锁仍被当前线程持有，就释放锁
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }

    @Override
    public void getRedissonRedLock() {
        // 也可以是其他类型锁
        RLock lock1 = redissonClient.getFairLock("lock1");
        RLock lock2 = redissonClient.getFairLock("lock2");
        RLock lock3 = redissonClient.getFairLock("lock3");
        RLock lock4 = redissonClient.getFairLock("lock4");
        // 同时加锁：lock1 lock2 lock3 lock4
        RedissonRedLock redLock = new RedissonRedLock(lock1,lock2,lock3,lock4);
        try {
            // 超过一半的锁都加上了才算成功
            // 最多等待10毫秒，获取锁8毫秒后自动释放
            boolean lockFlag = redLock.tryLock(10L, 8L, TimeUnit.SECONDS);
            if (lockFlag) {
                // 获得锁，执行一些操作
            }
        } catch (Exception e){
            log.error("发生异常,详细信息：",e);
        } finally {
            // 如果锁仍被当前线程持有，就释放锁
            if (redLock.isHeldByCurrentThread()) {
                redLock.unlock();
            }
        }
    }

    @Override
    public void getLock() {
        RLock lock = redissonClient.getLock("lock");
        try {
            // 超过一半的锁都加上了才算成功
            // 最多等待10毫秒，获取锁8毫秒后自动释放
            boolean lockFlag = lock.tryLock(10L, 8L, TimeUnit.SECONDS);
            if (lockFlag) {
                // 获得锁，执行一些操作
            }
        } catch (Exception e){
            log.error("发生异常,详细信息：",e);
        } finally {
            // 如果锁仍被当前线程持有，就释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void getReadWriteLock() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("lock");
        // 写锁
        RLock wLock = rwLock.writeLock();
        // 读锁
        RLock rLock1 = rwLock.readLock();
        RLock rLock2 = rwLock.readLock();

        try {
            // 最多等待10毫秒，获取锁8毫秒后自动释放
            boolean wLockFlag = wLock.tryLock(10L, 8L, TimeUnit.SECONDS);
            boolean rLockFlag1 = rLock1.tryLock(10L, 8L, TimeUnit.SECONDS);
            boolean rLockFlag2 = rLock2.tryLock(10L, 8L, TimeUnit.SECONDS);
            if (wLockFlag) {
                // 获得锁，执行一些操作
            }
            if (rLockFlag1) {
                // 获得锁，执行一些操作
            }
            if (rLockFlag2) {
                // 获得锁，执行一些操作
            }
        } catch (Exception e) {
            log.error("发生异常,详细信息：",e);

        } finally {
            // 如果锁仍被当前线程持有，就释放锁
            if (wLock.isHeldByCurrentThread()) {
                wLock.unlock();
            }
            if (rLock1.isHeldByCurrentThread()) {
                rLock1.unlock();
            }
            if (rLock2.isHeldByCurrentThread()) {
                rLock2.unlock();
            }
        }
    }

    @Override
    public void getCountDownLatch() {
        // 设置计数器
        RCountDownLatch setCountDownLatch = redissonClient.getCountDownLatch("num");
        setCountDownLatch.trySetCount(5);

        // 获取计数器
        RCountDownLatch getCountDownLatch = redissonClient.getCountDownLatch("num");
        try {
            // 如果当前计数器值不为0，则等待(阻塞当前线程，将当前线程加入阻塞队列)
            getCountDownLatch.await();
            // 在timeout的时间之内阻塞当前线程,时间一过则当前线程可以执行
            // await(long timeout, TimeUnit unit);
            // 计数器为0，开始执行
            // doSomeThing()
        } catch (Exception e){
            log.error("发生异常,详细信息：",e);
        }

        // 其他线程使用计数器
        RCountDownLatch otherLock = redissonClient.getCountDownLatch("num");
        // //对计数器进行递减1操作，当计数器递减至0时，当前线程会去唤醒阻塞队列里的所有线程
        otherLock.countDown();
    }

    @Override
    public void operateSemaphore() {
        //和锁一样  随便指定一个名字 只要名字相同获取的就是同一个信号量
        RSemaphore semaphore = redissonClient.getSemaphore("semaphore");
        // 设置信号量的值
        // semaphore.trySetPermits(10);
        // 阻塞式方法  获取成功执行下面  获取不成功就一直在这一句卡住
        // semaphore.acquire();
        boolean b = semaphore.tryAcquire();
        // 占成功了   redis里这个信号量的值-1
        if(b){
            //执行业务
            log.info("semaphore =>信号量有值 获取成功  可以执行业务");
            // 执行完毕，redis中信号量的值+1
            semaphore.release();
        }else{
            log.info("semaphore => 没有获取到信号量");
        }

    }

    @Override
    public void getPermitExpirableSemaphore() {
        RPermitExpirableSemaphore semaphore = redissonClient.getPermitExpirableSemaphore("semaphore");
        try {
            // 获取一个信号量，有效期为2秒
            String permitId = semaphore.acquire(2*1000,TimeUnit.SECONDS);
            if (Objects.nonNull(permitId)){
                // doSomeThing()
                semaphore.release(permitId);
            }

        } catch (Exception e){
            log.error("发生异常,详细信息：",e);
        }

    }
}
