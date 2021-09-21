package com.mood.userservice.jpa;


import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

import java.util.List;

public interface UserGradeRepository extends CrudRepository<UserGradeEntity, Long> {
    @Nullable
    UserGradeEntity findByGradeUid(String gradeUid);
    @Nullable
    UserGradeEntity findByGradeType(String gradeType);
    @Nullable
    List<UserGradeEntity> findAllByDisabled(boolean disabled);
}
