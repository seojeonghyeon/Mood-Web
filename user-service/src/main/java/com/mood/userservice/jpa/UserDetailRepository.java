package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface UserDetailRepository extends CrudRepository<UserDetailEntity, Long> {
    UserDetailEntity findByUserUid(String userUid);
    UserDetailEntity findByUserAgeBetweenAndUserGroupAndGenderAndOther_MAndOther_WAndMaxDistanceAndRecentLoginTime(
            int minAge, int maxAge, int userGroup, boolean gender, boolean other_M, boolean other_W, int maxDistance,
            LocalDateTime recentLoginTime);
    UserDetailEntity findAllByOther_MAndOther_WAndBetweenMinAgeAndMaxAge(boolean other_W, boolean other_m);
}