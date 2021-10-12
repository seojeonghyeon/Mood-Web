package com.mood.postservice.service;

import com.mood.postservice.dto.HashtagDto;
import com.mood.postservice.dto.PostDto;

import java.util.List;

public interface PostService {
    boolean registPost(PostDto postDto);
    boolean checkUserUid(String postUid);
    List<PostDto> getPostByType(String postUid, String postType, int page);
}
