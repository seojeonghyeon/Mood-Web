package com.mood.postservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface HashtagRepository extends CrudRepository<HashtagEntity, Long> {
    Optional<Iterable<HashtagEntity>> findByPostIdAndPostUid(String postId, String postUid);
    Optional<Iterable<HashtagEntity>> findByHashtagNameAndDisabled(String hashtagName, boolean disabled);
    Optional<Iterable<HashtagEntity>> findByPostUidAndDisabled(String postUid, boolean disabled);
    Optional<HashtagEntity> findByPostUidAndDisabledAndHashtagName(String postUid, boolean disabled, String hashtagName);
}
