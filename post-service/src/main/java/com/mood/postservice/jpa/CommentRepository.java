package com.mood.postservice.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostIdAndDisabledOrderByCommentGroup(String postId, String disabled, Pageable pageable);
    Optional<CommentEntity> findByPostIdAndDisabledAndCommentIdAndCommentUid(String postId, boolean disabled, String commentId, String commentUid);
    Optional<CommentEntity> findByPostIdAndCommentIdAndDisabled(String postId, String commentId, boolean disabled);
    Optional<Iterable<CommentEntity>> findByPostIdOrderByCommentClass(String postId);
    @Query(
            value = "SELECT count(comment_group) "+
                    "FROM comments " +
                    "WHERE post_id=:postId ",
            nativeQuery = true
    )
    int countByPostId(
            @Param("postId") String postId
    );

//    @Query(
//            value = "SELECT count(post_id) "+
//                    "FROM comments " +
//                    "WHERE post_id=:postId " +
//                    "AND comment_group=:commentGroup" +
//                    "AND disabled=:disabled",
//    nativeQuery = true
//            )
//    int countByPostIdAndCommentGroupAndDisabled(
//            @Param("postId") String postId,
//            @Param("commentGroup") String commentGroup,
//            @Param("disabled") boolean disabled
//    );
}
