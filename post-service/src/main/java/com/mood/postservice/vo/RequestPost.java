package com.mood.postservice.vo;

import lombok.Data;

import java.util.List;

@Data
public class RequestPost {
    private String postId;
    private String postUid;
    private String locationENG;
    private String locationKOR;
    private String postImage;
    private String postContents;

    private String commentContents;
    private String commentClass;
    private String commentGroup;

    private List<RequestHashtag> requestHashtags;
    private List<RequestLike> requestLikes;
    private List<RequestComment> requestComments;
}
