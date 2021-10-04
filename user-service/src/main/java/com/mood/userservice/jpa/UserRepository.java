package com.mood.userservice.jpa;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserUid(String userUid);
    Optional<UserEntity> findByEmailAndPhoneNum(String email, String phoneNum);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhoneNum(String phoneNum);
    Optional<UserEntity> findTop1ByPhoneNumOrderByRecentLoginTime(String phoneNum);
    Optional<UserEntity> findByCreditEnabledAndPhoneNum(boolean creditenabled, String phoneNum);
    Optional<Iterable<UserEntity>> findByUserGrade(String userGrade);
    int countByDisabledIsFalse();
}
