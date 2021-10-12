package com.mood.postservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDetailRepository extends CrudRepository<UserDetailEntity, Long> {
    Optional<UserDetailEntity> findByUserUid(String postUid);
}