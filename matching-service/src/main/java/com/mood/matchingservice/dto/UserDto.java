package com.mood.matchingservice.dto;

import com.mood.matchingservice.jpa.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    //User's Identity Value
    private String userUid;

    //User's Email
    private String email;

    //User's Nickname
    private String nickname;

    //User's Password
    private String password;

    private String phoneNum;

    private String birthdate;

    private boolean gender;

    //Matching gender is user's hope. User' hope is Man, then oter_m is true.
    private boolean otherM;
    private boolean otherW;

    //Question Value between 0 ~ 5
    private double respect;
    private double contact;
    private double date;
    private double communication;
    private double sex;

    //Question of String
    private String work;
    private String happy;
    private String dating;

    //Profile's Image, All is string value.
    private String profileImage;
    private String profileImageIcon;

    //Location Name, Eng. Alert a Kor in need.
    private String locationENG;
    private String locationKOR;
    private double latitude;
    private double longitude;

    //Sublocation Name, Eng for User's grade on VIP . Alert a Kor in need.
    private String subLocationENG;
    private String subLocationKOR;
    private double subLatitude;
    private double subLongitude;

    //User's Grade. View only VIP or Normal.  newbie(14days)->basic->heavy, VIP
    private String userGrade;
    private LocalDateTime gradeStart;
    private LocalDateTime gradeEnd;

    //For matching, min and max age is hoped matching.
    private int userAge;
    private int minAge;
    private int maxAge;

    //For matching, max distance is hoped matching.
    private int maxDistance;

    //Classification, user group.
    private double userGroup;

    //pay Crash
    private int coin;
    private int ticket;

    //Login Count
    private int loginCount;
    private LocalDateTime createTimeAt;
    private LocalDateTime recentLoginTime;

    //User's matching time
    //watching ad, then nextMatchingTime is 15 update
    //no watching ad, then next matching 6

    private LocalDateTime matchingTime;
    private LocalDateTime nextMatchingTime;

    //User is trying disabled a user's account, then reset 1 time. default is true.
    private boolean resetMatching;

    //Credit password
    private String creditPwd;

    //Credit passing. isTrue, then can changing the password
    private boolean creditEnabled;

    //Lock User's account
    private boolean userLock;

    private String userLockReasons;

    //User's account is disabled. default is false.
    private boolean disabled;


    public void settingUserDto(UserEntity userEntity){
        this.email=userEntity.getEmail();
        this.nickname=userEntity.getNickname();
        this.password=userEntity.getEncryptedPwd();
        this.phoneNum=userEntity.getPhoneNum();
        this.birthdate=userEntity.getBirthdate();
        this.profileImage=userEntity.getProfileImage();
        this.profileImageIcon=userEntity.getProfileImageIcon();
        this.coin=userEntity.getCoin();
        this.ticket=userEntity.getTicket();
        this.loginCount=userEntity.getLoginCount();
        this.createTimeAt=userEntity.getCreateTimeAt();
        this.recentLoginTime=userEntity.getRecentLoginTime();
        this.matchingTime=userEntity.getMatchingTime();
        this.nextMatchingTime=userEntity.getNextMatchingTime();
        this.resetMatching = userEntity.isResetMatching();
        this.creditPwd = userEntity.getCreditPwd();
        this.creditEnabled=userEntity.isCreditEnabled();
        this.userLock=userEntity.isUserLock();
        this.disabled=userEntity.isDisabled();
    }
}
