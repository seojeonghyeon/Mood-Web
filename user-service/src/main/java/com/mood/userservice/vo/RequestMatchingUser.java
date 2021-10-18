package com.mood.userservice.vo;

import lombok.Data;

@Data
public class RequestMatchingUser {
    private String userUid;

    private String profileImage;

    private String profileIcon;

    private String nickname;

    private String birthdate;

    private String locationKOR;
    private String locationENG;

    private double physicalDistance;

    private int respect;

    private int contact;

    private int date;

    private int communication;

    private int sex;

}
