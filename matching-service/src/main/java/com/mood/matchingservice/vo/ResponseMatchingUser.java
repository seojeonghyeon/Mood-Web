package com.mood.matchingservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMatchingUser {
    private String userUid;

    private String profileImage;

    private String profileImageIcon;

    private String nickname;

    private String birthdate;

    private String locationENG;

    private String locationKOR;

    private double physicalDistance;

    private int respect;

    private int contact;

    private int date;

    private int communication;

    private int sex;

    private MatchingData matchingData;
}