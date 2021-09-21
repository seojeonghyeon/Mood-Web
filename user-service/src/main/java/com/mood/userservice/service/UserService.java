package com.mood.userservice.service;

import com.mood.userservice.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserDetailsByEmail(String userName);
    boolean checkUserPhoneNumber(UserDto userDto);
    String getEmailByPhoneNum(UserDto userDto);
    boolean getCertification(UserDto userDto);
    void sendCreditNumber(String phoneNum);
    boolean resetPassword(UserDto userDto);
}
