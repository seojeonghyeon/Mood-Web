package com.mood.userservice.service;

import com.mood.userservice.client.MatchingServiceClient;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.*;
import com.mood.userservice.service.search.MatchingUsers;
import com.mood.userservice.service.search.MoodDistance;
import com.mood.userservice.vo.MatchingData;
import com.mood.userservice.vo.RequestMatchingUser;
import com.mood.userservice.vo.ResponseMatchingUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class MatchingServiceImpl implements MatchingService{

    UserRepository userRepository;
    UserDetailRepository userDetailRepository;
    UserGradeRepository userGradeRepository;
    Environment env;
    MatchingServiceClient matchingServiceClient;
    CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public MatchingServiceImpl(UserRepository userRepository, UserDetailRepository userDetailRepository,
                               UserGradeRepository userGradeRepository, Environment env,
                               MatchingServiceClient matchingServiceClient, CircuitBreakerFactory circuitBreakerFactory){
        this.userRepository = userRepository;
        this.userGradeRepository = userGradeRepository;
        this.env=env;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.userDetailRepository = userDetailRepository;
    }

    @Override
    public void updateMatchingUsers(UserDto userDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDetailEntity updateUserDetailEntity = mapper.map(userDto, UserDetailEntity.class);
        MoodDistance moodDistance = new MoodDistance();
        MatchingUsers matchingUser = new MatchingUsers(userRepository, userDetailRepository, userGradeRepository);

        //Matching Users, 2
        //Count percentage and get MatchingUsers
        Map<String, Integer> count = matchingUser.percentageCalculate(userDto);
        List<UserDetailEntity> matchingUsers = matchingUser.getMatchingUsers(userDto, count);
        List<RequestMatchingUser> matchingUserList = new ArrayList<RequestMatchingUser>();
        for(UserDetailEntity userDetailEntity : matchingUsers){
            Optional<UserEntity> optional = userRepository.findByUserUid(userDetailEntity.getUserUid());
            if(optional.isPresent()) {
                UserEntity userEntity = optional.get();
                RequestMatchingUser requestMatchingUser = mapper.map(userDetailEntity, RequestMatchingUser.class);
                requestMatchingUser.setProfileIcon(userEntity.getProfileImageIcon());
                requestMatchingUser.setProfileImage(userEntity.getProfileImage());
                requestMatchingUser.setPhysicalDistance(distance(userDto.getLatitude(), userDto.getLongitude(),
                        userDetailEntity.getLatitude(), userDetailEntity.getLongitude()));
                requestMatchingUser.setNickname(userEntity.getNickname());

                UserDto mathcingUserDto = mapper.map(requestMatchingUser, UserDto.class);
                MatchingData matchingData = new MatchingData();
                matchingData.setMoodDistance(moodDistance.search(userDto, mathcingUserDto));
                matchingData.setMatchingTime(LocalDateTime.now());

                requestMatchingUser.setMatchingData(matchingData);
                matchingUserList.add(requestMatchingUser);
            }
        }

        log.info("Before call matching microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseMatchingUser> matchingList
                = circuitBreaker.run(()->matchingServiceClient.updateMatchingUsers(matchingUserList),
                throwable -> new ArrayList<>());
        log.info("After called matching microservice");

    }

    //two point distance
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        return 6371*Math.acos(Math.cos(deg2rad(lat1))*Math.cos(deg2rad(lat2))*Math.cos(deg2rad(lon2-lon1))+Math.sin(deg2rad(lat1))*Math.sin(deg2rad(lat2)));
    }
    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}
