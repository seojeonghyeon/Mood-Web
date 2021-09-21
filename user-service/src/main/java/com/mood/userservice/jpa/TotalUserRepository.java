package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import java.io.Serializable;

public interface TotalUserRepository extends CrudRepository<TotalUserEntity, Long> {
    TotalUserEntity findTotalUserEntityByDisabled(boolean disabled);
}
