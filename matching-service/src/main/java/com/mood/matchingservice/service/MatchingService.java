package com.mood.matchingservice.service;


import com.mood.matchingservice.dto.MatchingUserDto;
import com.mood.matchingservice.dto.UserDto;
import com.mood.matchingservice.vo.ResponseMatchingUser;

import java.util.List;

public interface MatchingService {
    MatchingUserDto createMatchingUsers(MatchingUserDto matchingUser);

    List<ResponseMatchingUser> getMatchingUsers(String userUid);

    void updateMatchingUsers(MatchingUserDto matchingUserDto);

    List<ResponseMatchingUser> updateMatchingUsers(UserDto userDto, int number);

    void updateMatchingTime(String userUid);

    boolean updateResetMatchingTime(String userUid);
}
