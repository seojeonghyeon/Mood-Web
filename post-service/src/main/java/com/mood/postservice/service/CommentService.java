package com.mood.postservice.service;

import com.mood.postservice.dto.CommentDto;

public interface CommentService {
    boolean registComment(CommentDto commentDto);
}
