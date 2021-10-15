package com.mood.postservice.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByPostIdAndDisabled(String postId, boolean disabled);
    Optional<LikeEntity> findByLikeUidAndPostIdAndCommentId(String likeUid, String postId, String commentId);
}
