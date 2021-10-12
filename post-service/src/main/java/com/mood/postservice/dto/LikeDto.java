package com.mood.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDto {
    private String likeId;
    private String likeUid;
    private String postId;
    private String commentId;
    private LocalDateTime likeTime;
    private boolean disabled;
}
