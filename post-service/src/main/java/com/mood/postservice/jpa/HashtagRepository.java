package com.mood.postservice.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<HashtagEntity, Long> {
    Optional<Iterable<HashtagEntity>> findByPostIdAndPostUid(String postId, String postUid);
    Optional<Iterable<HashtagEntity>> findByHashtagNameAndDisabled(String hashtagName, boolean disabled);
    Optional<Iterable<HashtagEntity>> findByPostUidAndDisabled(String postUid, boolean disabled);
    Optional<HashtagEntity> findByPostUidAndDisabledAndHashtagName(String postUid, boolean disabled, String hashtagName);
    List<HashtagEntity> findByHashtagNameAndDisabledOrderByHashingTimeDesc(String hashtagName, boolean disabled, Pageable pageable);
}
