package com.lucky.luckyojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lucky.luckyojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.lucky")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.lucky.luckyojbackendserviceclient.service"})
public class LuckyojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuckyojBackendQuestionServiceApplication.class, args);
    }

}
