package com.mood.lockservice.service;

import com.mood.lockservice.dto.LockUserDto;

public interface LockService {
    LockUserDto createLockUser(LockUserDto lockUserDto);
    LockUserDto getLockUser(LockUserDto lockUserDto);
    LockUserDto updateLockUser(LockUserDto lockUserDto);
}
