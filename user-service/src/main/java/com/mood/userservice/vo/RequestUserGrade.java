package com.mood.userservice.vo;

import lombok.Data;

@Data
public class RequestUserGrade {
    private String gradeType;
    private String gradePercent;
    private int gradeDate;
}
