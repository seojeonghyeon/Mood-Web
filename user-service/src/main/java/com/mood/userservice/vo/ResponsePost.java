package com.mood.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePost {
    private String postId;
    private String profileImageIcon;
    private String postUid;
    private String nickname;
    private String location;
    private int postLikeCount;
    private int postCommentCount;
    private LocalDateTime postTime;
    private String postImage;
    private String postContents;
}
