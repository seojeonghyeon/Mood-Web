package com.mood.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class RequestLockUser {
    @Size(min = 2)
    private String lockUserUid;

    @Size(min = 2)
    private String lockType;

    @Size(min = 2)
    private String lockReasons;

    @Size(min = 2)
    private String referUid;
}
