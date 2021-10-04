package com.mood.userservice.scheduler;

import com.mood.userservice.jpa.UserEntity;
import com.mood.userservice.jpa.UserGradeEntity;
import com.mood.userservice.service.UserGradeService;
import com.mood.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserGradeScheduler {

    UserService userService;
    UserGradeService userGradeService;

    @Autowired
    public UserGradeScheduler(UserService userService, UserGradeService userGradeService){
        this.userService=userService;
        this.userGradeService=userGradeService;
    }

    //every day hours 0 check User Grade
    @Scheduled(cron = "0 10 0 * * *")
    public void updateUserGrade(){
        List<UserGradeEntity> listUserGrade = userGradeService.getUserGrade();
        for (UserGradeEntity userGradeEntity : listUserGrade){
            List<UserEntity> listUser = userService.getUserGrade(userGradeEntity);
            for(UserEntity userEntity : listUser){
                if(LocalDateTime.now().isAfter(userEntity.getGradeEnd())){
                    if(userGradeEntity.getGradeType().equals("newbie")){
                    }
                }
            }
        }
    }
}
