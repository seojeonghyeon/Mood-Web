package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<com.mood.userservice.jpa.UserEntity, Long> {
    com.mood.userservice.jpa.UserEntity findByUserId(String userId);
    com.mood.userservice.jpa.UserEntity findByEmail(String username);
}
