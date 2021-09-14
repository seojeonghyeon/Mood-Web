package com.mood.matchingservice.service;

import com.mood.matchingservice.dto.MatchingUserDto;
import com.mood.matchingservice.jpa.MatchingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MatchingServiceImpl implements MatchingService{
    MatchingRepository matchingRepository;
    Environment env;
    CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public MatchingServiceImpl(MatchingRepository matchingRepository, Environment env, CircuitBreakerFactory circuitBreakerFactory){
        this.env=env;
        this.matchingRepository=matchingRepository;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public MatchingUserDto createMatchingUsers(MatchingUserDto matchingUser) {
        return null;
    }

    @Override
    public List<MatchingUserDto> getMatchingUsers() {

        return null;
    }

    @Override
    public void updateMatchingUsers(MatchingUserDto matchingUserDto) {

    }
}
