package com.mood.postservice.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByDisabledOrderByPostTimeDesc(boolean disabled, Pageable pageable);
    List<PostEntity> findByDisabledOrderByPostLikeCountDesc(boolean disabled, Pageable pageable);
    List<PostEntity> findByDisabledAndLocationENGOrderByPostTimeDesc(boolean disabled, String locationENG, Pageable pageable);
}
