package com.mood.userservice.scheduler;

import com.mood.userservice.jpa.UserDetailEntity;
import com.mood.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAgeScheduler {

    UserService userService;

    @Autowired
    public UserAgeScheduler(UserService userService){
        this.userService=userService;
    }

    @Scheduled(cron = "0 20 0 1 1 *")
    public void updateUserAge(){
        List<UserDetailEntity> userDetailEntities = userService.getByAll();
        for ( UserDetailEntity userDetailEntity : userDetailEntities){
            userDetailEntity.setUserAge(userDetailEntity.getUserAge()+1);
            userService.updateUserAge(userDetailEntity);
        }
    }
}
