package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

public interface TotalUserRepository extends CrudRepository<TotalUserEntity, Long> {
    TotalUserEntity findTotalUserEntityByDisabledIsFalse();
}
