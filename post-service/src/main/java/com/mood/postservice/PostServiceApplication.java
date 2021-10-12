package com.mood.postservice;

import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;


@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class PostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }

    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }


    @PostConstruct
    public void started(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("현재 시각 : "+new Date());
    }

}
