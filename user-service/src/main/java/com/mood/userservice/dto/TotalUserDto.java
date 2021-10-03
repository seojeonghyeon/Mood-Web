package com.mood.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TotalUserDto {
    private Long id;
    private boolean disabled;
    private int totaluser;
    private LocalDateTime createdAt;
}
