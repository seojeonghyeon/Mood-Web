package com.mood.userservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BlockUserDto {
    private String blockUid;
    private String userUid;
    private String phoneNum;
    private LocalDateTime blockTime;
    private boolean disabled;
}
