package com.mood.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    //post 식별자
    private String postId;

    //post 게시자
    private String postUid;
    private String profileImageIcon;
    private String nickname;

    //post 내용
    private String postImage;
    private String locationENG;
    private String locationKOR;
    private String postContents;
    private int postLikeCount;
    private int postCommentCount;
    private LocalDateTime postTime;
    private List<CommentDto> responseCommentInfoList;
    private List<HashtagDto> hashtagDtos;
    private List<LikeDto> likeDtos;

    private boolean disabled;
}
