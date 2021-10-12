package com.mood.postservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserUid(String userUid);
}
