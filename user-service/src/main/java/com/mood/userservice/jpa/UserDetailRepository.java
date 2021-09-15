package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDetailRepository extends CrudRepository<UserDetailEntity, Long> {
    UserDetailEntity findByUserUid(String userUid);
    //Woman
    List<UserDetailEntity> findTop20ByUserGroupAndGenderAndOtherWAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime(
            int userGroup, boolean gender, boolean otherW, int maxDistance, String userGrade, boolean disabled, LocalDateTime recentLoginTime,
            int minAge, int maxAge);
    List<UserDetailEntity> findTop25ByUserGroupAndGenderAndOtherWAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime(
            int userGroup, boolean gender, boolean otherW, int maxDistance, String userGrade,boolean disabled, LocalDateTime recentLoginTime,
            int minAge, int maxAge);
    List<UserDetailEntity> findTop30ByUserGroupAndGenderAndOtherWAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime(
            int userGroup, boolean gender, boolean otherW, int maxDistance, String userGrade,boolean disabled,LocalDateTime recentLoginTime,
            int minAge, int maxAge);

    //Man
    List<UserDetailEntity> findTop20ByUserGroupAndGenderAndOtherMAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime(
            int userGroup, boolean gender, boolean otherW, int maxDistance, String userGrade,boolean disabled,LocalDateTime recentLoginTime,
            int minAge, int maxAge);
    List<UserDetailEntity> findTop25ByUserGroupAndGenderAndOtherMAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime(
            int userGroup, boolean gender, boolean otherW, int maxDistance, String userGrade,boolean disabled,LocalDateTime recentLoginTime,
            int minAge, int maxAge);
    List<UserDetailEntity> findTop30ByUserGroupAndGenderAndOtherMAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime(
            int userGroup, boolean gender, boolean otherW, int maxDistance, String userGrade,boolean disabled,LocalDateTime recentLoginTime,
            int minAge, int maxAge);
}