package com.mood.userservice.dto;

import lombok.Data;

@Data
public class RatePlanDto {
    private String rateplanId;

    private String rateplanType;

    private String productId;

    private int months;

    private boolean disabled;
}
