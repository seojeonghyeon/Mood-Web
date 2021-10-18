package com.mood.matchingservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends CrudRepository<MatchingEntity, Long> {
    Optional<List<MatchingEntity>> findByUserUidAndDisabled(String userUid, boolean disabled);
    Optional<MatchingEntity> findByMatchingIdAndUserUidAndDisabled(String matchingId, String userUid, boolean disabled);
}
