package com.mood.userservice.service;

import com.mood.userservice.dto.UserGradeDto;
import com.mood.userservice.jpa.UserGradeEntity;

import java.util.List;

public interface UserGradeService {
    boolean createUserGrade(UserGradeDto userGradeDto);
    boolean disabledUserGrade(UserGradeDto userGradeDto);
    List<UserGradeEntity> getUserGrade();
    String getUserGrade(String userGradeType);
    String printUserGrade(String userUid);
}
