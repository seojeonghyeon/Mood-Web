package com.mood.matchingservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserUid(String userUid);
    Optional<UserEntity> findByEmailAndPhoneNum(String email, String phoneNum);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findTop1ByPhoneNumOrderByRecentLoginTime(String phoneNum);
    Optional<UserEntity> findTop1ByCreditEnabledAndPhoneNumOrderByRecentLoginTime(boolean creditenabled, String phoneNum);
    Optional<Iterable<UserEntity>> findByUserGrade(String userGrade);
    Optional<UserEntity> findByNickname(String nickname);
    int countByDisabledIsFalse();
    Iterable<UserEntity> findByDisabled(boolean disabled);
}
