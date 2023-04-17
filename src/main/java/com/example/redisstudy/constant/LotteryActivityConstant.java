package com.example.redisstudy.constant;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: 抽奖活动常量类
 * @createDate: 2023/4/12 9:09
 **/
public class LotteryActivityConstant {
    public static final String  DAY_PART_CONFIG_KEY="lotteryDayPart";

    // 午餐、晚餐开始时间
    public static final String  DAY_PART_CONFIG_DEFAULT="10:00,17:00";

    // 全天
    public static final Integer DAY_PART_ALL=0;

    // 中餐
    public static final Integer DAY_PART_LUNCH=1;

    // 晚餐
    public static final Integer DAY_PART_DINNER=2;


    public static final byte YES=1;

    public static final byte NO=0;

    public static final String LOTTERY_ACTIVITY_CONFIG="lottery.config:%s";

    // rewardCode  date   dayPart
    public static final String FIELD_LIMIT_COUNT="limit_%s.d.%s.p.%s";

    public static final String FIELD_DAY_PART_RULE="rule_%s.p.%s";

    public static final String FIELD_CONFIG="config";
    // activityCode  date   dayPart
    public static final String LUCKY_DRAW_COUNT="lottery.count.%s:d%s.p%s";

    public static final String LOTTERY_RECORD_CARD="lottery.record:%s:%s";

    public static final String LOTTERY_RECORD_RANK="lottery.rank:%s";
}
