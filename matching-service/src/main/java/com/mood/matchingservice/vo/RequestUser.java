package com.mood.matchingservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

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
    private boolean other_M;

    //User's hoping a Matching Gender Woman="True"
    private boolean other_W;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private int respect;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private int contact;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private int date;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private int communication;

    @Min(value = 0, message = "Min Value is 0")
    @Max(value = 5, message = "Max value is 5")
    private int sex;

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
    private String location;

    private double latitude;

    private double longitude;

    @Size(min = 1, message = "Location must be equeal or grater than 1 characters")
    private String subLocation;

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
}
