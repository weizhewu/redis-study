package com.example.redisstudy.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: 活动
 * @createDate: 2023/4/11 16:34
 **/
@Data
public class LotteryActivityConfig {

    private Integer id;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 0 消费后抽奖 1 锦鲤抽奖
     */
    private Byte activityType;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityBegin;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEnd;

    /**
     * 状态
     */
    private Integer status;


    /**
     * 活动顺序
     */
    private Integer showOrder;


    /**
     * 活动描述
     */
    private String activityDesc;

    /**
     * 活动统计日期开始
     */
    private LocalDateTime activityStatsBegin;

    /**
     * 活动统计日期结束
     */
    private LocalDateTime activityStatsEnd;

    /**
     * 活动门槛 例如消费门槛
     */
    private String activityLimit;

    /**
     * 1 付费会员 0 全部会员
     */
    private Boolean vipLimit;


    private List<LotteryActivityRewardConfig> rewardConfigList;

}
