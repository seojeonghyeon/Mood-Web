package com.mood.postservice.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCommentInfo {
    private String commentId;
    private String commentUid;
    private String nickname;
    private String profileImageIcon;
    private String commentContents;
    private String commentClass;
    private String commentGroup;
    private LocalDateTime commentTime;
    private int commentLikeCount;
}
