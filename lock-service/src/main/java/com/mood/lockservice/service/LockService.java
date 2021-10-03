package com.mood.lockservice.service;

import com.mood.lockservice.dto.LockUserDto;
import com.mood.lockservice.jpa.LockUserEntity;

import java.util.List;

public interface LockService {
    boolean createLockUser(LockUserDto lockUserDto);
    Iterable<LockUserEntity> getLockUser(String lockUserUid);
    boolean updateLockUser(String userUid, String lockType);
    boolean checkUserUid(String userUid);
}
