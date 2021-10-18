package com.mood.matchingservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Long> {
    Optional<UserDetailEntity> findByUserUid(String userUid);

    int countByUserGroupAndUserGradeAndDisabled(double userGroup, String userGrade, boolean disabled);

    int countByUserGroupAndDisabled(int userGroup, boolean disabled);

    int countByDisabled(boolean disabled);

    //Matching for Man
    @Query(
            value = "SELECT * "+
            "FROM userdetails " +
                    "WHERE user_group=:userGroup AND user_grade=:userGrade AND gender=:gender " +
                    "AND otherm=:otherM AND user_lock=:userLock AND disabled=:disabled " +
                    "AND user_age BETWEEN :minAge AND :maxAge "+
                    "AND recent_login_time >= :recentLoginTime "+
                    "AND 6371*acos(cos(radians(latitude))*cos(radians(:latitude))*cos(radians(:longitude)-radians(longitude))+sin(radians(latitude))*sin(radians(:latitude))) <= :maxDistance " +
                    "AND user_uid != :userUid " +
                    "ORDER BY recent_login_time LIMIT :limitNumber",
            nativeQuery = true
    )
    List<UserDetailEntity> findByOtherM(
            @Param("userGroup") double userGroup, @Param("userGrade") String userGrade, @Param("gender") boolean gender,
            @Param("otherM") boolean otherM, @Param("userLock") boolean userLock, @Param("disabled") boolean disabled,
            @Param("minAge") int minAge, @Param("maxAge") int maxAge,
            @Param("recentLoginTime") LocalDateTime recentLoginTime, @Param("latitude") double latitude,
            @Param("longitude") double longitude, @Param("maxDistance") int maxDistance, @Param("userUid") String userUid, int limitNumber
    );

    //Matching for Woman
    @Query(
            value = "SELECT * "+
                    "FROM userdetails " +
                    "WHERE user_group=:userGroup AND user_grade=:userGrade AND gender=:gender " +
                    "AND otherw=:otherW AND user_lock=:userLock AND disabled=:disabled " +
                    "AND user_age BETWEEN :minAge AND :maxAge "+
                    "AND recent_login_time >= :recentLoginTime "+
                    "AND 6371*acos(cos(radians(latitude))*cos(radians(:latitude))*cos(radians(:longitude) -radians(longitude))+sin(radians(latitude))*sin(radians(:latitude))) <= :maxDistance " +
                    "AND user_uid != :userUid " +
                    "ORDER BY recent_login_time LIMIT :limitNumber",
            nativeQuery = true
    )
    List<UserDetailEntity> findByOtherW(
            @Param("userGroup") double userGroup, @Param("userGrade") String userGrade, @Param("gender") boolean gender,
            @Param("otherW") boolean otherW, @Param("userLock") boolean userLock, @Param("disabled") boolean disabled,
            @Param("minAge") int minAge, @Param("maxAge") int maxAge,
            @Param("recentLoginTime") LocalDateTime recentLoginTime, @Param("latitude") double latitude,
            @Param("longitude") double longitude, @Param("maxDistance") int maxDistance, @Param("userUid") String userUid, int limitNumber
    );
}