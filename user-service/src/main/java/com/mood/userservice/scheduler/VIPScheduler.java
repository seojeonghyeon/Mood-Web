package com.mood.userservice.scheduler;

import com.mood.userservice.jpa.UserEntity;
import com.mood.userservice.jpa.UserGradeEntity;
import com.mood.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class VIPScheduler {
    UserService userService;

    @Autowired
    public VIPScheduler(UserService userService){
        this.userService=userService;
    }

    //every day hours 0 give 10 coin
    @Scheduled(cron = "0 0 0 * * *")
    public void updateVIPCoin(){
        log.info("Before give the coin for VIP : "+ LocalDateTime.now());
        UserGradeEntity userGradeEntity = userService.getVIPType();
        List<UserEntity> list = userService.getUserGrade(userGradeEntity);
        for(UserEntity userEntity : list){
            userService.updateVIPCoin(userEntity, 10);
        }
        log.info("After give the coin for VIP : "+ LocalDateTime.now());
    }
}
