package com.mood.lockservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseLockUser {
    @Size(min = 2)
    private String lockUserUid;

    @Size(min = 2)
    private String lockType;

    @Size(min = 2)
    private String lockReasons;

    @Size(min = 2)
    private String referUid;
}
