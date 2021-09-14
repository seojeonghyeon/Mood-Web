package com.mood.matchingservice.service;


import com.mood.matchingservice.dto.MatchingUserDto;

import java.util.List;

public interface MatchingService {
    MatchingUserDto createMatchingUsers(MatchingUserDto matchingUser);

    List<MatchingUserDto> getMatchingUsers();

    void updateMatchingUsers(MatchingUserDto matchingUserDto);
}
