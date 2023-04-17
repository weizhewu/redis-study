package com.example.redisstudy.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: 抽奖规则
 * @createDate: 2023/4/11 16:35
 **/
@Data
public class LotteryActivityRewardRules {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     *
     */
    private Integer rewardId;

    /**
     * 奖项唯一编码
     */
    private String rewardCode;

    /**
     * 1 午餐 2 晚餐
     */
    private Integer dayPart;

    /**
     * 奖项个数
     */
    private Integer rewardCount;

    /**
     * 0 随机 1 指定
     */
    private Integer rewardRuleType;

    /**
     * 指定的中奖位次
     */
    private String rewardRuleStr;

    /**
     * 中奖概率
     */
    private BigDecimal rewardRatio;
}

