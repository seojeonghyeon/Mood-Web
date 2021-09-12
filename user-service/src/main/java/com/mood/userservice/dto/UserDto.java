package com.mood.userservice.dto;

import com.mood.userservice.vo.ResponseMatchingUser;
import com.mood.userservice.vo.ResponsePost;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

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
    private boolean other_M;
    private boolean other_W;

    //Question Value between 0 ~ 5
    private int respect;
    private int contact;
    private int date;
    private int communication;
    private int sex;

    //Question of String
    private String work;
    private String happy;
    private String dating;

    //Profile's Image, All is string value.
    private String profileImage;
    private String profileImageIcon;

    //Location Name, Eng. Alert a Kor in need.
    private String location;
    private double latitude;
    private double longitude;

   //Sublocation Name, Eng for User's grade on VIP . Alert a Kor in need.
    private String subLocation;
    private double subLatitude;
    private double subLongitude;

    //User's Grade. View only VIP or Normal.  newbie(14days)->basic->heavy, VIP
    private String userGrade;
    private LocalDateTime gradeStart;
    private LocalDateTime gradeEnd;

    //For matching, min and max age is hoped matching.
    private int minAge;
    private int maxAge;

    //For matching, max distance is hoped matching.
    private int maxDistance;

    //Clustering, user group.
    private int userGroup;

    //pay Crash
    private int coin;
    private int ticket;

    //Login Count
    private int loginCount;
    private LocalDateTime createTimeAt;
    private LocalDateTime recentLoginTime;

    //User is trying disabled a user's account, then reset 1 time. default is true.
    private boolean resetMatching;

    //Credit password
    private String creditPwd;

    //Lock User's account
    private boolean userLock;

    //User's account is disabled. default is false.
    private boolean disabled;

    //Matching Users
    private List<ResponseMatchingUser> MatchingUsers;

    //User's Posts
    private List<ResponsePost> PostArticles;
}