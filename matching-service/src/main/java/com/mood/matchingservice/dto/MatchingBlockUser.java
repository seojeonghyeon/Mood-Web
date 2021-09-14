package com.mood.matchingservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MatchingBlockUser {
    //matching block Uid
    private String matchingBlockUid;

    //User sets matching block
    private String userUid;

    //matching blocking user
    private String otherUserUid;

    //blocking time
    private LocalDateTime blockTime;

    //disabled
    private boolean disabled;
}
