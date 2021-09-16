package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByUserUid(String userUid);
    UserEntity findByEmail(String email);
    UserEntity findByPhoneNum(String phoneNum);
}
