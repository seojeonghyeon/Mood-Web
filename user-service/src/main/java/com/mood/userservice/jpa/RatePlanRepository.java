package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RatePlanRepository extends CrudRepository<RatePlanEntity, Long> {
    Optional<RatePlanEntity> findByProductId(String productId);
}
