package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PurchaseRepository extends CrudRepository<PurchaseEntity, Long> {
    Optional<PurchaseEntity> findByUserUid();
}
