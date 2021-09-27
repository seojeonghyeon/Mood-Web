package com.mood.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseLockUser {
    private boolean exist;

    @Size(min = 2)
    private String lockUserUid;

    @Size(min = 2)
    private String lockType;

    @Size(min = 2)
    private String lockReasons;

    @Size(min = 2)
    private String referUid;

    private String fromUserUid;

    private LocalDateTime activeTime;
}
