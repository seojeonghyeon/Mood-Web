package com.mood.userservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Long> {
    UserDetailEntity findByUserUid(String userUid);

    int countByUserGroupAndUserGradeAndDisabled(double userGroup, String userGrade, boolean disabled);

    int countByUserGroupAndDisabled(int userGroup, boolean disabled);

    int countByDisabled(boolean disabled);

    //Matching for Man
    @Query(
            value = "SELECT DISTINCT * "+
            "FROM userdetails " +
                    "WHERE userGroup=:userGroup AND userGrade=:userGrade AND gender=:gender " +
                    "AND otherM=:otherM AND userLock=:userLock AND disabled=:disabled " +
                    "AND userAge BETWEEN :minAge AND :maxAge "+
                    "AND recentLoginTime >= :recentLoginTime "+
                    "AND 6371*acos(cos(radians(latitude))*cos(radians(:latitude))*cos(radians(:longitude)" +
                    "-radians(longitude))+sin(radians(latitude))*sin(radians(:latitude))) > :maxDistance " +
                    "ORDER BY recentLoginTime LIMIT :limitNumber",
            nativeQuery = true
    )
    List<UserDetailEntity> findDistinctByOtherM(
            @Param("userGroup") double userGroup, @Param("userGrade") String userGrade, @Param("gender") boolean gender,
            @Param("otherM") boolean otherM, @Param("userLock") boolean userLock, @Param("disabled") boolean disabled,
            @Param("minAge") int minAge, @Param("maxAge") int maxAge,
            @Param("recentLoginTime") LocalDateTime recentLoginTime, @Param("latitude") double latitude,
            @Param("longitude") double longitude, @Param("maxDistance") int maxDistance, int limitNumber
    );

    //Matching for Woman
    @Query(
            value = "SELECT DISTINCT * "+
                    "FROM userdetails " +
                    "WHERE userGroup=:userGroup AND userGrade=:userGrade AND gender=:gender " +
                    "AND otherW=:otherW AND userLock=:userLock AND disabled=:disabled " +
                    "AND userAge BETWEEN :minAge AND :maxAge "+
                    "AND recentLoginTime >= :recentLoginTime "+
                    "AND 6371*acos(cos(radians(latitude))*cos(radians(:latitude))*cos(radians(:longitude)" +
                    "-radians(longitude))+sin(radians(latitude))*sin(radians(:latitude))) > :maxDistance " +
                    "ORDER BY recentLoginTime LIMIT :limitNumber",
            nativeQuery = true
    )
    List<UserDetailEntity> findDistinctByOtherW(
            @Param("userGroup") double userGroup, @Param("userGrade") String userGrade, @Param("gender") boolean gender,
            @Param("otherW") boolean otherW, @Param("userLock") boolean userLock, @Param("disabled") boolean disabled,
            @Param("minAge") int minAge, @Param("maxAge") int maxAge,
            @Param("recentLoginTime") LocalDateTime recentLoginTime, @Param("latitude") double latitude,
            @Param("longitude") double longitude, @Param("maxDistance") int maxDistance, int limitNumber
    );
}