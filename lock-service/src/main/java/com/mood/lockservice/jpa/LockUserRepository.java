package com.mood.lockservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LockUserRepository extends CrudRepository<LockUserEntity, Long> {
    Optional<LockUserEntity> findByLockUid(String lockUid);
    Iterable<LockUserEntity> findByLockUserUid(String lockUserUid);
}
