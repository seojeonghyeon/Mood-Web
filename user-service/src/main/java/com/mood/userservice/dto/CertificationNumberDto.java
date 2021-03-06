package com.mood.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CertificationNumberDto {
    private String certificationUid;
    private boolean disabled;
    private String phoneNum;
    private int creditNumber;
    private LocalDateTime createdAt;
}
