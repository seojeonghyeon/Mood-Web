package com.mood.postservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface HashtagRepository extends CrudRepository<HashtagEntity, Long> {
    Optional<Iterable<HashtagEntity>> findByPostIdAndDisabled(String postId, boolean disabled);
    Optional<Iterable<HashtagEntity>> findByHashtagNameAndDisabled(String hashtagName, boolean disabled);
    Optional<Iterable<HashtagEntity>> findByPostUidAndDisabled(String postUid, boolean disabled);
}
