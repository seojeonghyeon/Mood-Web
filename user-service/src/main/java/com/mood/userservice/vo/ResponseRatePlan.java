package com.mood.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseRatePlan {
    private String rateplanId;

    private String rateplanType;

    private String productId;

    private int months;

    private boolean disabled;
}
