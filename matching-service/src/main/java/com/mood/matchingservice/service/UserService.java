package com.mood.matchingservice.service;

import com.mood.matchingservice.jpa.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> findByDisabled();
}
