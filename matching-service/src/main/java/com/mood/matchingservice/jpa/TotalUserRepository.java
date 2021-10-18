package com.mood.matchingservice.jpa;

import org.springframework.data.repository.CrudRepository;

public interface TotalUserRepository extends CrudRepository<TotalUserEntity, Long> {
    TotalUserEntity findTotalUserEntityByDisabled(boolean disabled);
}
