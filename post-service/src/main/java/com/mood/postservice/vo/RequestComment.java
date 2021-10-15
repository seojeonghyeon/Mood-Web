package com.mood.postservice.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestComment {
    private String postId;
    private String commentId;
    private String commentContents;
    private int commentClass;
    private int commentGroup;
}
