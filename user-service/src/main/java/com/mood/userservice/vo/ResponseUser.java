package com.mood.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {
    private String userUid;
    private String email;
    private String nickname;
    private String password;
    private String phoneNum;
    private String birthdate;
    private boolean gender;
    private boolean other_M;
    private boolean other_W;
    private double respect;
    private double contact;
    private double date;
    private double communication;
    private double sex;
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
    private int coin;
    private int ticket;
    private boolean userLock;
    private String userLockReasons;
}
