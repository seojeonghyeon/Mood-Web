package com.mood.userservice.dto;

import lombok.Data;

@Data
public class MatchingUserDto {
    private String userUid;
    private double distance;
}
