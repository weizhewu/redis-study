package com.example.redisstudy.service;

import com.example.redisstudy.common.result.ResponseResult;
import com.example.redisstudy.common.result.ResultCode;
import com.example.redisstudy.entity.LotteryParam;

import javax.annotation.Resource;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: 抽奖活动
 * @createDate: 2023/4/11 15:42
 **/
public interface LotteryActivityService {
    /**
     * 开始抽奖
     * @param lotteryParam 抽奖活动所需参数
     * @return ResponseResult
     */
    ResponseResult<ResultCode> startLottery(LotteryParam lotteryParam);
}
