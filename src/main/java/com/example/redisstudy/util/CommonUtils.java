package com.example.redisstudy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: 日期处理工具类
 * @createDate: 2023/4/11 17:38
 **/
@Slf4j
public class CommonUtils {
    public static final String DATETIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public  static Date localDateToDate(LocalDate localDate){
        return   Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public  static  Date dayAdd(Date now,int add){

        return localDateToDate(LocalDate.ofEpochDay(now.getTime()).plusDays(add));
    }



    public  static Date dateTimeStrToDate(String str){

        SimpleDateFormat dateFormat=new SimpleDateFormat(DATETIME_DEFAULT_FORMAT);
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            log.error("日期转换失败,str={}",str,e);
            return  null;
        }
    }


    public static List<String> getBetweenDate(String dateStart, String dateEnd) {
        LocalDate start = LocalDate.parse(dateStart, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(dateEnd, DateTimeFormatter.ISO_LOCAL_DATE);
        List<String> dateList = new ArrayList<>();
        if (start.isAfter(end)) {
            LocalDate tmp = start;
            start = end;
            end = tmp;

        }

        while (isBeforeEqual(start, end)) {
            dateList.add(start.format(DateTimeFormatter.ISO_LOCAL_DATE));
            start = start.plusDays(1);
        }

        return dateList;
    }

    public  static  String maxDate(String date1 ,String date2){

        if(LocalDate.parse(date1).isBefore(LocalDate.parse(date2))){
            return  date2;
        }
        return  date1;

    }

    public  static  String maxDate(LocalDate date1 ,LocalDate date2){

        if(date1.isBefore(date2)){
            return  date2.format(DateTimeFormatter.ISO_DATE);
        }
        return  date1.format(DateTimeFormatter.ISO_DATE);

    }


    public  static  boolean isBeforeEqual(LocalDate date1 ,LocalDate date2){
        return date1.isBefore(date2)||date1.isEqual(date2);
    }


    private  static ObjectMapper objectMapper;

    static {
        objectMapper=new ObjectMapper();
    }

    public static <T> String getJsonStr(T o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error("json序列号失败", e);

        }
        return "";
    }

    public static <T> T getJsonObject(String value ,Class<T> tClass) {

        try {
            return objectMapper.readValue(value,tClass);
        } catch (Exception e) {
            log.error("json返序列号失败", e);
        }
        return null;

    }

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.of(2023,4,11,17,53);
        LocalDateTime end = LocalDateTime.of(2023,4,13,17,53);
        List<String> dateList = CommonUtils.getBetweenDate(start.toLocalDate().format(DateTimeFormatter.ISO_DATE),
                end.toLocalDate().format(DateTimeFormatter.ISO_DATE));
        dateList.forEach(System.out::println);
    }


}

