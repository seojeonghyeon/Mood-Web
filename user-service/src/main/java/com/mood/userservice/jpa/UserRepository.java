package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;


public interface UserRepository extends CrudRepository<UserEntity, Long> {
    @Nullable
    UserEntity findByUserUid(String userUid);
    @Nullable
    UserEntity findByEmail(String email);
    @Nullable
    UserEntity findByPhoneNum(String phoneNum);
    @Nullable
    UserEntity findByCreditEnabledAndPhoneNum(boolean creditenabled, String phoneNum);

    int countByDisabledIsFalse();
}
