package com.mood.postservice.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestComment {
    private String postId;
    private String commentContents;
    private int commentClass;
    private int commentGroup;
}
