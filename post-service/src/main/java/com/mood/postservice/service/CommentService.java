package com.mood.postservice.service;

import com.mood.postservice.dto.CommentDto;

import java.util.List;

public interface CommentService {
    boolean registComment(CommentDto commentDto);
    boolean deleteComment(CommentDto commentDto);
    void updateLikeCount(String postId, String commentId, int number);
    List<CommentDto> getCommentByPostId(String postId);
}
