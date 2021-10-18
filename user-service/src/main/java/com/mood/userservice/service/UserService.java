package com.mood.userservice.service;

import com.mood.userservice.dto.PurchaseDto;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.UserDetailEntity;
import com.mood.userservice.jpa.UserEntity;
import com.mood.userservice.jpa.UserGradeEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserDetailsByEmail(String userName);
    boolean checkUserEmail(String email);
    String getEmailByPhoneNum(UserDto userDto);
    boolean getCertification(UserDto userDto);
    void sendRegistCreditNumber(String phoneNum, String hashkey);
    boolean checkRegistCertification(String phoneNum, String numberId);
    boolean sendCreditNumber(String phoneNum, String hashkey);
    boolean resetPassword(UserDto userDto);
    UserDto getUserInfo(String userUid);
    boolean findByUserUid(String userUid);
    boolean checkRegistCertificationIsTrue(String phoneNum);
    boolean updateUserLock(String userUid, boolean lockBoolean);
    boolean checkCertification(String phoneNum, String numberId);
    String findByUserUid(String email, String phoneNum);
    void updateVIPCoin(UserEntity userEntity, int coin);
    UserGradeEntity getVIPType();
    List<UserEntity> getUserGrade(UserGradeEntity userGradeEntity);
    String getGradeUid(String type);
    String getGradeType(String uid);
    int updateUserAge(UserDto userDto);
    int updateUserAge(String birth);
    List<UserDetailEntity> getByAll();
    void updateUserAge(UserDetailEntity userDetailEntity);
    void updateUserGrade(UserEntity userEntity, boolean vipDown);
    UserDto updateUserGradeVIP(PurchaseDto purchaseDto);
    boolean updateUserSettings(UserDto userDto);
    UserDto getUser(String userUid);
    UserDetailEntity getUserDetail(String userUid);
    boolean checkNickname(UserDto userDto);
    boolean updatePhoneNum(String userUid, String phoneNum);
    List<UserDto> getUsers();
    void updateUserGroup(UserDto userDto);
}
