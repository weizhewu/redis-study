package com.example.redisstudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @version 1.0.0
 * @author: wei-zhe-wu
 * @description: RestTemplate配置
 * @createDate: 2023/4/7 11:41
 **/
@Configuration
public class RestTemplateConfig {

    // 配置 RestTemplate
    @Bean
    public RestTemplate registerTemplate() {
        //这个地方需要配置消息转换器，不然收到消息后转换会出现异常
        return new RestTemplate(getFactory());
    }

    //设置工厂连接超时的一些配置
    private SimpleClientHttpRequestFactory getFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 设置连接超时
        factory.setConnectTimeout(15000);
        // 设置读取超时
        factory.setReadTimeout(5000);
        return factory;
    }

}