package com.mood.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentInfoDto {
    private String commentId;
    private String commentUid;
    private String commentContents;
    private int commentClass;
    private int commentGroup;
    private LocalDateTime commentTime;
    private boolean disabled;
}
