package com.mood.userservice.service;

import com.mood.userservice.dto.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserDetailsByEmail(String userName);
    boolean checkUserEmail(UserDto userDto);
    boolean checkUserPhoneNumber(UserDto userDto);
    String getEmailByPhoneNum(UserDto userDto);
    boolean getCertification(UserDto userDto);
    void sendRegistCreditNumber(String phoneNum, String hashkey);
    boolean checkRegistCertification(String phoneNum, String numberId);
    void sendCreditNumber(String phoneNum, String hashkey);
    boolean resetPassword(UserDto userDto);
    UserDto getUserInfo(UserDto userDto);
}
