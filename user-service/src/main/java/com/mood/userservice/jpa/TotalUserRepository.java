package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

public interface TotalUserRepository extends CrudRepository<TotalUserEntity, Long> {
    @Nullable
    TotalUserEntity findTotalUserEntityByDisabled(boolean disabled);
}
