package com.mood.lockservice.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class LockUserDto {
    private String lockUid;
    private String lockUserUid;
    private String lockType;
    private String lockReasons;
    private String referUid;
    private boolean lockUserDisabled;
    private LocalDateTime activeTime;
}
