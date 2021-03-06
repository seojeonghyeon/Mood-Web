package com.mood.postservice.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseComment {
    private String commentId;
    private String commentUid;
    private String nickname;
    private String profileImageIcon;
    private String commentContents;
    private int commentClass;
    private int commentGroup;
    private LocalDateTime commentTime;
    private int commentLikeCount;
}
