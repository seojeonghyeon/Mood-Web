package com.mood.userservice.service;

import com.mood.userservice.dto.RatePlanDto;
import com.mood.userservice.jpa.RatePlanEntity;

public interface RatePlanService {
    void addRatePlan(RatePlanDto ratePlanDto);
    RatePlanDto getRatePlan(String productId);
    Iterable<RatePlanEntity> getRatePlans();
}
