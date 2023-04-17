package com.example.redisstudy.service.impl;

import com.example.redisstudy.common.result.ResponseResult;
import com.example.redisstudy.common.result.ResultCode;
import com.example.redisstudy.constant.LotteryActivityConstant;
import com.example.redisstudy.constant.RedisConstant;
import com.example.redisstudy.entity.*;
import com.example.redisstudy.service.LotteryActivityService;
import com.example.redisstudy.service.RedisService;
import com.example.redisstudy.util.CommonUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: 抽奖活动
 * @createDate: 2023/4/11 15:42
 **/
@Service
@Slf4j
public class LotteryActivityServiceImpl implements LotteryActivityService {
    @Resource
    private RedisService redisService;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 随机判断是否成功
     */
    private static final boolean[] confirms = new boolean[]{true,false};

    /**
     * 中奖概率
     */
    private final static Map<String, BigDecimal> DEFAULT_LOTTERY_RATIO = new ConcurrentHashMap<>();

    // 本地缓存 缓存2min
    Cache<String, String> configCache =
            Caffeine.newBuilder().initialCapacity(10).maximumSize(10000).expireAfterWrite(2, TimeUnit.MINUTES).build();


    @Override
    public ResponseResult<ResultCode> startLottery(LotteryParam lotteryParam) {
        if (Objects.isNull(lotteryParam)){
            return ResponseResult.failure(ResultCode.PARAM_IS_BLANK);
        }
        // 以及其他字段判空
        if (Objects.isNull(lotteryParam.getActivityCode()) ||
            Objects.isNull(lotteryParam.getCardNo()) ||
            Objects.isNull(lotteryParam.getMobile())){
            return ResponseResult.failure(ResultCode.PARAM_NOT_COMPLETE);
        }
        // 1、开始抽奖前准备
        // 关于redisson的相关介绍，请看文档
        RLock lock = redissonClient.getFairLock("LOCK.Lottery."+lotteryParam.getActivityCode());
        if (Objects.isNull(lock)){
            return ResponseResult.failure(ResultCode.LOTTERY_FAIL);
        }

        try {
            // 公平锁，最多锁五秒
            // 锁的时间根据业务调整
            boolean flag = lock.tryLock(5, TimeUnit.SECONDS);
            if (flag){
                // 2、开始抽奖
                return startLotteryActivity(lotteryParam);
            } else {
                return ResponseResult.failure(ResultCode.LOTTERY_ACTIVITY_BUSY);
            }
        } catch (Exception e){
            log.error("抽奖失败，activityCode={}", lotteryParam.getActivityCode(), e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return ResponseResult.failure(ResultCode.LOTTERY_FAIL);
    }



    private ResponseResult<ResultCode> startLotteryActivity(LotteryParam lotteryParam){
        // 参数前面已经验证完了，这里就不验证了
        // 1、获取活动配置————需要
        String redisKey4Config = String.format(LotteryActivityConstant.LOTTERY_ACTIVITY_CONFIG, lotteryParam.getActivityCode());
        LotteryActivityConfig lotteryActivityConfig = redisService.getRedisTemplate().<String, LotteryActivityConfig>opsForHash().get(redisKey4Config,LotteryActivityConstant.FIELD_CONFIG);
        // 2、根据互动配置进行一系列规则判断，例如
        String activityCode = lotteryParam.getActivityCode();
        // 2.1、验证活动状态
        if (!isActivityStart(lotteryParam.getActivityCode())){
            return ResponseResult.failure(ResultCode.LOTTERY_ACTIVITY_NOT_START_OR_END);
        }
        // 2.2、验证用户状态
        LotteryQueryParam lotteryQueryParam = new LotteryQueryParam();
        lotteryQueryParam.setActivityCode(activityCode);
        lotteryQueryParam.setMobile(lotteryParam.getMobile());
        lotteryQueryParam.setCardNo(lotteryParam.getCardNo());
        if (!isPersonAllowed(lotteryQueryParam)){
            return ResponseResult.failure(ResultCode.USER_AUTHENTICATE_FAIL);
        }
        // 3、开始抽奖
        // 3.1、获取抽奖位次，这里判断的比较简单，正常情况下，应当和其他字段进行组合，例如：日期、餐段等等
        String redisKey = String.format(LotteryActivityConstant.LUCKY_DRAW_COUNT, activityCode);
        Long openSequence = redisService.increment(redisKey);
        if (Objects.isNull(openSequence)) {
            return ResponseResult.failure(ResultCode.LOTTERY_FAIL);
        }
        redisService.expire(String.valueOf(openSequence),1,TimeUnit.DAYS);
        // 3.2、按照奖励优先级进行排序
        List<LotteryActivityRewardConfig> rewardConfigList = lotteryActivityConfig.getRewardConfigList();
        rewardConfigList.sort(Comparator.comparing(LotteryActivityRewardConfig::getRewardOrder));
        Integer dayPart = calcDayPart();
        LotteryActivityRewardConfig finalRewardConfig = null;
        LotteryActivityRewardRules finalRewardRule = null;
        for (LotteryActivityRewardConfig rewardConfig : rewardConfigList){
            // 按照奖励优先级，依次抽奖
            LotteryActivityRewardRules luckyRule = lucky(lotteryActivityConfig, rewardConfig, dayPart, openSequence);
// 中奖
            if (null != luckyRule && StringUtils.isNotEmpty(luckyRule.getRewardRuleStr())) {
                String limitField = getRedisField4Limit(rewardConfig.getRewardCode(), luckyRule.getRewardRuleStr(),
                        luckyRule.getDayPart());
                // 是否对应奖项还有个数 不足则认为未抽中 暂时也不继续抽
                Long increment =
                        (long) redisService.decrementMapValue(redisKey4Config, limitField, -1);
                // 真正抽中
                // 20230328 只有真正抽中才不继续走 上一步抽中但是奖项限额不足则继续抽下一个奖项
                if (increment >= 0) {
                    finalRewardRule = luckyRule;
                    finalRewardConfig = rewardConfig;
                    break;
                }
            }
        }
        if (null != finalRewardConfig) {
            // todo 异步操作
            // 送奖券
            // 送奖券
            send();
            // 真正抽中
            saveLotteryRecord();
        } else {
            // 未抽中
            saveLotteryRecord();
        }
        if (null != finalRewardConfig) {
            return  ResponseResult.success();
        } else {
            return  ResponseResult.failure(ResultCode.LOTTERY_NOT_SUCCESS);
        }
    }


    /**
     * 根据活动编码，确定活动开始情况
     * @param activityCode 活动编码
     * @return true:开始 false:未开始
     */
    private boolean isActivityStart(String activityCode){
        log.info("开始判断当前活动是否开始或者其他条件，活动编码为：{}",activityCode);
        boolean res;
        // 如果不符合，就返回false
        // 先用随机数判断是否符合
        Random random = new Random();
        int index = random.nextInt(2);
        res = confirms[index];
        return res;

    }

    /**
     * 确定用户资格
     * @param lotteryQueryParam 活动查询实体类
     * @return true：有资格 false：无资格
     */
    private boolean isPersonAllowed(LotteryQueryParam lotteryQueryParam){
        log.info("开始判断当前用户是否满足活动参与条件，用户信息：{}",lotteryQueryParam);
        boolean res;
        // 经过各种判断
        // 如果不符合，就返回false
        // 先用随机数判断是否符合
        Random random = new Random();
        int index = random.nextInt(2);
        res = confirms[index];
        return res;
    }

    /**
     * 按奖项配置抽奖
     * @param config 活动配置
     * @param rewardConfig 奖项配置
     * @param dayPart 时段
     * @param openSequence 抽奖位次 按天 按时段
     * @return 是否抽中
     */
    private LotteryActivityRewardRules lucky(LotteryActivityConfig config,LotteryActivityRewardConfig rewardConfig,Integer dayPart,long openSequence){
        int rewardCycle = rewardConfig.getRewardCycle();
        // 1、确定可抽奖日期范围
        List<String> dateList = getDateList(rewardCycle, rewardConfig.getRewardCycleStr(), config.getActivityBegin(),
                config.getActivityEnd());
        // 判断当前时间是否在可抽奖日期范围内
        LocalDate currDate = LocalDate.now();
        String dateStr = currDate.format(DateTimeFormatter.ISO_DATE);
        String dateStrKey = "";
        for (String date : dateList){
            // 获取指定范围的可抽奖日期范围
            List<String> dates = resolveDate(date);
            if (dates.contains(dateStr)){
                dateStrKey = date;
                break;
            }
        }
        // 非中奖日期
        if (dateStrKey.isEmpty()){
            return null;
        }
        // 从rewardConfig中获取RewardRulesList，然后使用stream()函数将其转换为Map<Byte,LotteryActivityRewardRules>，
        // 其中key是LotteryActivityRewardRules的dayPart属性，value是LotteryActivityRewardRules本身，如果有重复的key，则使用新的value覆盖旧的value。
        Map<Integer,LotteryActivityRewardRules> dayPartMap = rewardConfig.getRewardRulesList()
                .stream()
                .collect(Collectors.toMap(
                      LotteryActivityRewardRules::getDayPart,
                      Function.identity(), (o,n)->n
                ));
        // 按照时段 获取对应的配置
        LotteryActivityRewardRules rule = dayPartMap.getOrDefault(dayPart,dayPartMap.get(LotteryActivityConstant.DAY_PART_ALL));
        // 未有匹配的规则
        if (Objects.isNull(rule)){
            return null;
        }
        // 记录限制
        rule.setRewardRuleStr(dateStr);
        if (ifLucky(rule, openSequence)) {
            return rule;
        }
        return null;
    }


    /**
     * 根据rewardCycle，rewardCycleStr确定奖励日期
     * @param rewardCycle 日期类型
     * @param rewardCycleStr 日期内容
     * @param begin 活动开始
     * @param end 活动结束
     * @return 日期范围
     */
    private List<String> getDateList(int rewardCycle, String rewardCycleStr, LocalDateTime begin, LocalDateTime end){
        List<String> dateList = new ArrayList<>();
        switch (rewardCycle){
            case 0:// 每天 按照活动开始日期到活动结束日期
                dateList.addAll(CommonUtils.getBetweenDate(begin.toLocalDate().format(DateTimeFormatter.ISO_DATE),
                        end.toLocalDate().format(DateTimeFormatter.ISO_DATE)));
                break;
            case 1:// 指定日期
            case 2:// 日期范围
                dateList.addAll(Arrays.asList(rewardCycleStr.split(";")));
                break;
            default:return dateList;
        }
        return dateList;
    }

    /**
     *
     * @param rules
     * @param openSequence
     * @return
     */
    private boolean ifLucky(LotteryActivityRewardRules rules, long openSequence){
        Integer rewardRuleType = rules.getRewardRuleType();
        switch (rewardRuleType){
            case 0:// 随机
                BigDecimal rewardRatio = rules.getRewardRatio();
                if (Objects.isNull(rewardRatio)){
                    rewardRatio = DEFAULT_LOTTERY_RATIO.get(rules.getActivityCode());
                }
                if (Objects.isNull(rewardRatio)){
                    return false;
                }

                BigDecimal num = new BigDecimal(10).pow(rewardRatio.scale());
                long i = RandomUtils.nextLong(1, num.longValue());
                // 抽中
                if (i < rewardRatio.multiply(num).longValue()) {
                    return true;
                }
                break;
            // 指定中奖位次
            case 1: {
                String rewardRuleStr = rules.getRewardRuleStr();
                if (StringUtils.isNotEmpty(rewardRuleStr)) {
                    String[] split = rewardRuleStr.split(",");
                    // 次数 是否满足中奖位次
                    return Arrays.asList(split).contains(String.valueOf(openSequence));
                }
                break;
            }
            default:
                return false;
        }
        return false;
    }

    /**
     * 确定当前时间是哪个餐段
     * @return
     */
    private Integer calcDayPart() {
        LocalDateTime now = LocalDateTime.now();
        // String configValue = iosOrderMapper.getMiscConfigValue(LotteryActivityConstants.DAY_PART_CONFIG_KEY);
        String configValue = redisService.getObjectString(LotteryActivityConstant.DAY_PART_CONFIG_KEY);
        if (StringUtils.isEmpty(configValue)) {
            configValue = LotteryActivityConstant.DAY_PART_CONFIG_DEFAULT;

        }

        String dateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        String[] split = configValue.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time1 = LocalDateTime.parse(dateStr + " " + split[0] + ":00", formatter);
        LocalDateTime time2 = LocalDateTime.parse(dateStr + " " + split[1] + ":00", formatter);

        /*if(now.isBefore(time1)){
            return LotteryActivityConstants.DAY_PART_ALL;
        }*/
        if (now.isAfter(time1) && now.isBefore(time2)) {
            return LotteryActivityConstant.DAY_PART_LUNCH;
        }

        if (now.isAfter(time2)) {
            return LotteryActivityConstant.DAY_PART_DINNER;
        }
        return LotteryActivityConstant.DAY_PART_ALL;
    }

    // 送奖券
    private void send(){}

    private void saveLotteryRecord(){}

    /**
     * 将日期范围分解为更加准确的日期
     * @param dateStr 日期范围
     * @return 更加准确的日期
     */
    private List<String> resolveDate(String dateStr) {
        if (dateStr.contains(",")) {
            String[] split = dateStr.split(",");
            return CommonUtils.getBetweenDate(split[0], split[1]);
        }
        return Collections.singletonList(dateStr);
    }

    private String getRedisField4Limit(String rewardCode, String dateStr, Integer dayPart) {

        return String.format(LotteryActivityConstant.FIELD_LIMIT_COUNT, rewardCode, dateStr, dayPart);

    }
}
