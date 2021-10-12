package com.mood.postservice.jpa;


import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserGradeRepository extends CrudRepository<UserGradeEntity, Long> {
    Optional<UserGradeEntity> findByGradeUid(String gradeUid);
    Optional<UserGradeEntity> findByGradeType(String gradeType);
}
