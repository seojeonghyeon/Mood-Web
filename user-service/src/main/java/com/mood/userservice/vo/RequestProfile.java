package com.mood.userservice.vo;

import lombok.Data;

@Data
public class RequestProfile {
    private String profileImage;
    private String profileImageIcon;
    private String nickname;
    private String birthdate;
    private String location;
    private double physicalDistance;
    private String work;
    private String happy;
    private String dating;
    private double moodDistance;
    private int respect;
    private int contact;
    private int date;
    private int communication;
    private int sex;
}
