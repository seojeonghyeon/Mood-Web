package com.mood.matchingservice.scheduler;

import com.mood.matchingservice.jpa.UserEntity;
import com.mood.matchingservice.service.MatchingService;
import com.mood.matchingservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

public class AutoMatchingScheduler {
    MatchingService matchingService;
    UserService userService;

    @Autowired
    public AutoMatchingScheduler(MatchingService matchingService, UserService userService){
        this.matchingService=matchingService;
        this.userService=userService;
    }
    //every day hours 0 check
    @Scheduled(cron = "0 10 * * * *")
    public void update() {
        List<UserEntity> list = userService.findByDisabled();
        for(UserEntity userEntity : list){
            if(LocalDateTime.now().isBefore(userEntity.getNextMatchingTime()))
                matchingService.getMatchingUsers(userEntity.getUserUid());
        }
    }
}
