package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByUserUid(String userUid);
    UserEntity findByEmail(String email);
    UserEntity findByPhoneNum(String phoneNum);
}
