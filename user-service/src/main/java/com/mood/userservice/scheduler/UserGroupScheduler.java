package com.mood.userservice.scheduler;

import com.mood.userservice.dto.UserDto;
import com.mood.userservice.service.UserService;
import com.mood.userservice.service.classification.UserGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserGroupScheduler {
    UserService userService;

    @Autowired
    public UserGroupScheduler(UserService userService){
        this.userService = userService;
    }

    //every day hours 0 check User Grade
    @Scheduled(cron = "0 40 0 * * *")
    public void updateUserGroup() {
        List<UserDto> userDtoList = userService.getUsers();
        for(UserDto userDto : userDtoList)
            userService.updateUserGroup(userDto);
    }
}
