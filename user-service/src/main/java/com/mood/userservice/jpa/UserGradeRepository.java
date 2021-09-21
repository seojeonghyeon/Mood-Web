package com.mood.userservice.jpa;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

public interface UserGradeRepository extends CrudRepository<UserGradeEntity, Long> {
    UserGradeEntity findByGradeUid(String gradeUid);
    UserGradeEntity findByGradeType(String gradeType);
    List<UserGradeEntity> findAllByDisabled(boolean disabled);
}
