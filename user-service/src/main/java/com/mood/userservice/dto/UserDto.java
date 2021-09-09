package com.mood.userservice.dto;

import com.mood.userservice.vo.ResponseMatchingUser;
import com.mood.userservice.vo.ResponsePost;
import lombok.Data;
import java.util.List;

@Data
public class UserDto {
    private String userUid;
    private String email;
    private String nickname;
    private String password;
    private String phoneNum;
    private String birthdate;
    private boolean gender;
    private boolean other_M;
    private boolean other_W;
    private int respect;
    private int contact;
    private int date;
    private int communication;
    private int sex;
    private String work;
    private String happy;
    private String dating;
    private String profileImage;
    private String profileImageIcon;
    private String location;
    private double latitude;
    private double longitude;
    private String subLocation;
    private double subLatitude;
    private double subLongitude;
    private String userGrade;
    private int minAge;
    private int maxAge;
    private int maxDistance;
    private List<ResponseMatchingUser> MatchingUsers;
    private List<ResponsePost> PostArticles;
}
