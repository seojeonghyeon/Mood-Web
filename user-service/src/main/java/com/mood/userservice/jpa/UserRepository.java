package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserUid(String userUid);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhoneNum(String phoneNum);
    Optional<UserEntity> findByCreditEnabledAndPhoneNum(boolean creditenabled, String phoneNum);
    int countByDisabledIsFalse();
}
