package com.example.redisstudy.entity;

import lombok.Data;

import java.util.List;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: TODO
 * @createDate: 2023/4/11 17:00
 **/
@Data
public class LotteryActivityRewardConfig {

    private Integer id;

    /**
     *
     */
    private Integer activityId;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     *
     */
    private String rewardName;

    /**
     *
     */
    private String rewardCoupon;

    /**
     * 0 每天 1 固定日期 2 日期范围
     */
    private Integer rewardCycle;

    /**
     * 指定中奖日期规则
     */
    private String rewardCycleStr;

    /**
     * 榜单是否显示 1 显示 0 不显示
     */
    private Boolean rankShowFlag;


    /**
     * 奖励排序
     */
    private Byte rewardOrder;

    /**
     * 奖项唯一id，需生成
     */
    private String rewardCode;


    private List<LotteryActivityRewardRules> rewardRulesList;
}

