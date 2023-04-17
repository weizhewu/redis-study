package com.example.redisstudy.entity;

import lombok.Data;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: 抽奖活动所需参数
 * @createDate: 2023/4/11 15:44
 **/
@Data
public class LotteryParam {
    // 活动编码
    private String activityCode;

    // 手机号
    private String mobile;

    // 卡号
    private String cardNo;

    // 奖券id
    private String ticketId;

    // 抽奖人名字
    private String name;
}
