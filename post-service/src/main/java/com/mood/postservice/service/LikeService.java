package com.mood.postservice.service;

import com.mood.postservice.dto.LikeDto;

public interface LikeService {
    boolean updateLike(LikeDto likeDto);
}
