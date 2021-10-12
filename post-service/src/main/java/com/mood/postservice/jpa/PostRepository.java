package com.mood.postservice.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Optional<Iterable<PostEntity>> findByPostUidAndDisabledOrderByPostTimeDesc(String postUid, boolean disabled, Pageable pageable);
    Iterable<PostEntity> findByDisabledOrderByPostTimeDesc(boolean disabled, Pageable pageable);
    Iterable<PostEntity> findByDisabledOrderByPostLikeCountDesc(boolean disabled, Pageable pageable);
    Iterable<PostEntity> findByDisabledAndLocationENGOrderByPostTimeDesc(boolean disabled, String locationENG, Pageable pageable);
    Optional<PostEntity> findByPostIdAndDisabled(String postId, boolean disabled, Pageable pageable);
}
