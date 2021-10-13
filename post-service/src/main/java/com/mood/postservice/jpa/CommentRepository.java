package com.mood.postservice.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostIdAndDisabledOrderByCommentGroup(String postId, String disabled, Pageable pageable);
    @Query(
            value = "SELECT count(comment_group) "+
                    "FROM comments " +
                    "WHERE post_id=:postId ",
            nativeQuery = true
    )
    int countByPostId(
            @Param("postId") String postId
    );
}
