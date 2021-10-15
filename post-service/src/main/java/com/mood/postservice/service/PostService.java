package com.mood.postservice.service;

import com.mood.postservice.dto.HashtagDto;
import com.mood.postservice.dto.PostDto;

import java.util.List;

public interface PostService {
    boolean registPost(PostDto postDto, int HOCK);
    boolean updatePost(PostDto postDto, int HOCK);
    boolean deletePost(String postUid, String postId);
    boolean checkUserUid(String postUid);
    List<PostDto> getPostByType(String postUid, String postType, int page);
    List<PostDto> getPostsByPostUid(String postUid, int page);
    void updateCommentCount(String postId, int number);
    void updateLikeCount(String postId, int number);
    List<PostDto> getPostByHashtag(String hashtagName, int page);
    PostDto getPostByPostId(String postId);
}
