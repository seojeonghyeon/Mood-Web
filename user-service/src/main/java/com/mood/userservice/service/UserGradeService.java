package com.mood.userservice.service;

import com.mood.userservice.dto.UserGradeDto;

public interface UserGradeService {
    boolean createUserGrade(UserGradeDto userGradeDto);
    boolean disabledUserGrade(UserGradeDto userGradeDto);
}
