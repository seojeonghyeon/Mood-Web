package com.mood.userservice.jpa;


import org.springframework.data.repository.CrudRepository;

public interface UserGradeRepository extends CrudRepository<UserGradeEntity, Long> {
    UserGradeEntity findByGradeUid(String gradeUid);
    UserGradeEntity findByGradeType(String gradeType);
}
