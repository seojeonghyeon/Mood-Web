package com.mood.userservice.scheduler;

import com.mood.userservice.jpa.UserDetailEntity;
import com.mood.userservice.jpa.UserEntity;
import com.mood.userservice.jpa.UserGradeEntity;
import com.mood.userservice.service.UserGradeService;
import com.mood.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
        boolean vipDown = false;
        log.info("Before update user's grade : "+LocalDateTime.now());
        List<UserGradeEntity> listUserGrade = userGradeService.getUserGrade();
        for (UserGradeEntity userGradeEntity : listUserGrade){
            List<UserEntity> listUser = userService.getUserGrade(userGradeEntity);
            for(UserEntity userEntity : listUser){
                if(LocalDateTime.now().isAfter(userEntity.getGradeEnd())) {
                    String setGrade = "";
                    if (userGradeEntity.getGradeType().equals(NEWBIE))
                        setGrade = userGradeService.getUserGrade(BASIC);
                    else if (userGradeEntity.getGradeType().equals(VIP)){
                        if (userEntity.getLoginCount() >= 20) {
                            setGrade = userGradeService.getUserGrade(HEAVY);
                        }
                        else {
                            setGrade = userGradeService.getUserGrade(BASIC);
                        }
                        vipDown=true;
                    } else if (userGradeEntity.getGradeType().equals(HEAVY)) {
                        if (userEntity.getLoginCount() >= 15) {
                            setGrade = userGradeService.getUserGrade(HEAVY);
                        }
                        else {
                            setGrade = userGradeService.getUserGrade(BASIC);
                        }
                    } else if (userGradeEntity.getGradeType().equals(BASIC)) {
                        if (userEntity.getLoginCount() >= 20) {
                            setGrade = userGradeService.getUserGrade(HEAVY);
                        }
                        else {
                            setGrade = userGradeService.getUserGrade(BASIC);
                        }
                    }
                    userEntity.setUserGrade(setGrade);
                    userEntity.setGradeStart(LocalDateTime.now());
                    userEntity.setGradeEnd(LocalDateTime.now().plusDays(30));
                    userEntity.setLoginCount(0);
                    userService.updateUserGrade(userEntity, vipDown);
                }
            }
        }
        log.info("After update user's grade : "+LocalDateTime.now());
    }
}
