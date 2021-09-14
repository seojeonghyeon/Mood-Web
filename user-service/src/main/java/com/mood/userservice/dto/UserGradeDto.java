package com.mood.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserGradeDto {
    //grade uid
    private String gradeUid;

    //grade name
    private String gradeType;

    //grade percent
    private String gradePercent;

    //grade add date
    private int gradeDate;

    //disabled
    private boolean disabled;
}
