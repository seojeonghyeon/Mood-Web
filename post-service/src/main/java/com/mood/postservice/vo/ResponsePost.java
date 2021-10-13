package com.mood.postservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePost {
    private String postId;
    private String profileImageIcon;
    private String postUid;
    private String nickname;
    private String locationENG;
    private String locationKOR;
    private int postLikeCount;
    private int postCommentCount;
    private LocalDateTime postTime;
    private String postImage;
    private String postContents;
    private List<ResponseComment> responseCommentInfoList;
}
