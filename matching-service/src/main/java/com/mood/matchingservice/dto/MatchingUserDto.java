package com.mood.matchingservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchingUserDto {
    //Matching Uid
    private String matchingId;

    //Matching User
    private String userUid;

    //Matching User's other user Uid
    private String otherUserUid;

    //matching ending time
    private LocalDateTime matchingTime;

    //calculate the var
    private double moodDistance;

    //if other matching is enabled, then disabled
    private boolean disabled;
}
