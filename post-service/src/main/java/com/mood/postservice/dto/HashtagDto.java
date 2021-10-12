package com.mood.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HashtagDto {
    private String hashTagId;
    private String postId;
    private String postUid;
    private String hashtagName;
    private LocalDateTime hashingTime;
    private boolean disabled;
}
