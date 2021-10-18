package com.mood.userservice.scheduler;

import com.mood.userservice.service.TotalUserService;
import com.mood.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TotalUserScheduler {

    UserService userService;
    TotalUserService totalUserService;

    @Autowired
    public TotalUserScheduler(UserService userService, TotalUserService totalUserService){
        this.userService = userService;
        this.totalUserService = totalUserService;
    }

    //every day hours 0 check
    @Scheduled(cron = "0 30 0 * * *")
    public void updateTotalUser() {
        totalUserService.checkTotalUser();
    }
}
