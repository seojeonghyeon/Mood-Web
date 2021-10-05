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

    private final String VIP="VIP";
    private final String HEAVY="heavy";
    private final String BASIC="basic";
    private final String NEWBIE="newbie";

    UserService userService;
    UserGradeService userGradeService;

    @Autowired
    public UserGradeScheduler(UserService userService, UserGradeService userGradeService){
        this.userService=userService;
        this.userGradeService=userGradeService;
    }

    //every day hours 0 check User Grade
    @Scheduled(cron = "0 20 0 * * *")
    public void updateUserGrade(){
        List<UserGradeEntity> listUserGrade = userGradeService.getUserGrade();
        for (UserGradeEntity userGradeEntity : listUserGrade){
            List<UserEntity> listUser = userService.getUserGrade(userGradeEntity);
            for(UserEntity userEntity : listUser){
                if(LocalDateTime.now().isAfter(userEntity.getGradeEnd())) {
                    if (userGradeEntity.getGradeType().equals(NEWBIE))
                        userEntity.setUserGrade(userGradeService.getUserGrade(BASIC));
                    else if (userGradeEntity.getGradeType().equals(VIP)){
                        if (userEntity.getLoginCount() >= 20) userEntity.setUserGrade(userGradeService.getUserGrade(HEAVY));
                        else userEntity.setUserGrade(userGradeService.getUserGrade(BASIC));
                    } else if (userGradeEntity.getGradeType().equals(HEAVY)) {
                        if (userEntity.getLoginCount() >= 15) userEntity.setUserGrade(userGradeService.getUserGrade(HEAVY));
                        else userEntity.setUserGrade(userGradeService.getUserGrade(BASIC));
                    } else if (userGradeEntity.getGradeType().equals(BASIC)) {
                        if (userEntity.getLoginCount() >= 20) userEntity.setUserGrade(userGradeService.getUserGrade(HEAVY));
                        else userEntity.setUserGrade(userGradeService.getUserGrade(BASIC));
                    }
                    userEntity.setGradeStart(LocalDateTime.now());
                    userEntity.setGradeEnd(LocalDateTime.now().plusDays(30));
                    userEntity.setLoginCount(0);
                    userService.updateUserGrade(userEntity);
                }
            }
        }
    }
}
