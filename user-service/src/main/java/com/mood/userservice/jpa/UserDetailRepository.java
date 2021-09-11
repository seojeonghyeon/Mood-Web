package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

public interface UserDetailRepository extends CrudRepository<UserDetailEntity, Long> {
    UserEntity findByUserUid(String userUid);
}