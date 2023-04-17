package com.example.redisstudy.entity;

import lombok.Data;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: 活动查询实体类
 * @createDate: 2023/4/11 15:57
 **/
@Data
public class LotteryQueryParam {
    private String activityCode;

    private String cardNo;

    private String mobile;
}
