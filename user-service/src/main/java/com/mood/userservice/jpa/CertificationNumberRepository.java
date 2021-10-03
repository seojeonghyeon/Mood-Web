package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CertificationNumberRepository extends CrudRepository<CertificationNumberEntity, Long> {
    Optional<CertificationNumberEntity> findByPhoneNum(String phoneNum);
    Optional<CertificationNumberEntity> findByPhoneNumAndDisabled(String phoneNum, boolean disabled);
}
