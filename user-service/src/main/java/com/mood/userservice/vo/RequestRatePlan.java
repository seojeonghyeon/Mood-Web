package com.mood.userservice.vo;

import lombok.Data;

@Data
public class RequestRatePlan {
    private String rateplanId;

    private String rateplanType;

    private String productId;

    private int months;

    private boolean disabled;
}
