package com.mood.matchingservice.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchingData {
    private double moodDistance;
    private LocalDateTime matchingTime;
}
