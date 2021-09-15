package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlockUserRepository extends CrudRepository<BlockUserEntity, Long> {
    List<BlockUserEntity> findDistinctByUserUidAndDisabled(String UserUid, boolean disabled);
}
