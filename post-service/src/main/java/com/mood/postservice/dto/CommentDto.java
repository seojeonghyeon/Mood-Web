package com.mood.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    //식별
    private String commentId;

    //작성자
    private String commentUid;
    private String nickname;
    private String profileImageIcon;

    //내용
    private String commentContents;

    //위치
    private String postId;
    private int commentClass;
    private int commentGroup;

    //작성시간
    private LocalDateTime commentTime;
    private int commentLikeCount;

    //활성화여부
    private boolean disabled;
}
