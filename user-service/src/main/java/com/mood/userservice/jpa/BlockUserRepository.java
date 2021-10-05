package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlockUserRepository extends CrudRepository<BlockUserEntity, Long> {
    Iterable<BlockUserEntity> findDistinctByUserUidAndDisabled(String UserUid, boolean disabled);
    Optional<Iterable<BlockUserEntity>> findByUserUid(String userUid);
    Optional<BlockUserEntity> findByUserUidAndPhoneNum(String userUid, String phoneNum);
}
