package com.mood.userservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.swing.*;
import javax.validation.constraints.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class RequestUser {

    //User Identity Value
    private String userUid;

    @Size(min=2, message = "Email not be less than two characters")
    @Email
    private String email;

    @Size(min=2, message = "Name not be less than two characters")
    private String nickname;

    @Size(min = 8, message = "Password must be equeal or grater than 8 characters")
    private String password;

    //010-0000-0000
    @Size(min = 10, message = "phoneNumber must be equeal or grater than 8 characters")
    private String phoneNum;

    //birthdate = "yyyy-mm-dd"
    @Size(min = 8, message = "Birth Date must be equeal or grater than 8 characters")
    private String birthdate;

    //Gender, Man = True, Woman = False
    private boolean gender;

    //User's hoping a Matching Gender Man="True"
    private boolean otherM;

    //User's hoping a Matching Gender Woman="True"
    private boolean otherW;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private double respect;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private double contact;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private double date;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private double communication;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private double sex;

    @Size(min = 1, message = "Answer must be equeal or grater than 1 characters")
    private String work;

    @Size(min = 1, message = "Answer must be equeal or grater than 1 characters")
    private String happy;

    @Size(min = 1, message = "Answer must be equeal or grater than 1 characters")
    private String dating;

    @Size(min = 1, message = "Image must be equeal or grater than 1 characters")
    private String profileImage;

    @Size(min = 1, message = "Image must be equeal or grater than 1 characters")
    private String profileImageIcon;

    @Size(min = 1, message = "Location must be equeal or grater than 1 characters")
    private String locationKOR;

    @Size(min = 1, message = "Location must be equeal or grater than 1 characters")
    private String locationENG;

    private double latitude;

    private double longitude;

    @Size(min = 1, message = "Location must be equeal or grater than 1 characters")
    private String subLocationKOR;

    @Size(min = 1, message = "Location must be equeal or grater than 1 characters")
    private String subLocationENG;

    private double subLatitude;

    private double subLongitude;

    @Size(min = 1, message = "User's grade must be equeal or grater than 1 characters")
    private String userGrade;

    @Min(value = 20, message = "Min Age is 20")
    @Max(value = 90, message = "Max Age is 90")
    private int minAge;

    @Min(value = 20, message = "Min Age is 20")
    @Max(value = 90, message = "Max Age is 90")
    private int maxAge;

    @Min(value = 10, message = "Min Age is 20")
    @Max(value = 90, message = "Max Age is 90")
    private int maxDistance;

    private String hashkey;

    private String numberId;
}
