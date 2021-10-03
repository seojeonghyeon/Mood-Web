package com.mood.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMatchingUser {
    private String userUid;

    private String profileImage;

    private String profileIcon;

    private String nickname;

    private String birthdate;

    private String location;

    private double physicalDistance;

    private int respect;

    private int contact;

    private int date;

    private int communication;

    private int sex;

    MatchingData matchingData;
}
