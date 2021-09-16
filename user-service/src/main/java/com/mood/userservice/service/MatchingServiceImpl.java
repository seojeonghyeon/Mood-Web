package com.mood.userservice.service;

import com.mood.userservice.client.MatchingServiceClient;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.*;
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

import javax.persistence.Column;
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
        Map<String, Integer> count = percentageCalculate(userDto);
        List<UserDetailEntity> matchingUsers = getMatchingUsers(userDto, count);
        List<RequestMatchingUser> matchingUserList = new ArrayList<RequestMatchingUser>();

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        for(UserDetailEntity userDetailEntity : matchingUsers){
            UserEntity userEntity = userRepository.findByUserUid(userDetailEntity.getUserUid());
            RequestMatchingUser requestMatchingUser = mapper.map(userDetailEntity, RequestMatchingUser.class);
            requestMatchingUser.setProfileIcon(userEntity.getProfileImageIcon());
            requestMatchingUser.setProfileImage(userEntity.getProfileImage());
            requestMatchingUser.setPhysicalDistance(distance(userDto.getLatitude(),userDto.getLongitude(),
                    userDetailEntity.getLatitude(), userDetailEntity.getLongitude(), "kilometer"));
            requestMatchingUser.setNickname(userEntity.getNickname());
            MatchingData matchingData = new MatchingData();

            requestMatchingUser.setMatchingData(matchingData);
            matchingUserList.add(requestMatchingUser);
        }

        log.info("Before call matching microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseMatchingUser> matchingList
                = circuitBreaker.run(()->matchingServiceClient.updateMatchingUsers(matchingUserList),
                throwable -> new ArrayList<>());
        log.info("After called matching microservice");

    }

    public List<UserDetailEntity> getMatchingUsers(UserDto userDto, Map<String, Integer> count){
        List<UserDetailEntity> getMatchingUsers = new ArrayList<UserDetailEntity>();
        //Man want to man and woman
        if((userDto.isGender()) && (userDto.isOtherM()) && (userDto.isOtherW())){
            getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 1, count));
            getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 1, count));
        }
        //Woman want to man and woman
        else if((!userDto.isGender()) && (userDto.isOtherM()) && (userDto.isOtherW())) {
            getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 1, count));
            getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 1, count));
        }
        //Man want to woman
        else if((userDto.isGender()) && (!userDto.isOtherM()) && (userDto.isOtherW()))
            getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 2, count));
        //Man want to man
        else if((userDto.isGender()) && (userDto.isOtherM()) && (!userDto.isOtherW()))
            getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 2, count));
        //Woman want to man
        else if((!userDto.isGender()) && (userDto.isOtherM()) && (!userDto.isOtherW()))
            getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 2, count));
        //Woman want to woman
        else if((!userDto.isGender()) && (!userDto.isOtherM()) && (userDto.isOtherW()))
            getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 2, count));
        return getMatchingUsers;
    }

    public Map<String, Integer> percentageCalculate(UserDto userDto){
        List<UserGradeEntity> userGradeEntities = userGradeRepository.findAllByDisabled(false);
        Map<String, Integer> result = new HashMap<String,  Integer>();
        for(UserGradeEntity i : userGradeEntities){
            int count = userDetailRepository.countByUserGroupAndUserGradeAndDisabled(
                    userDto.getUserGroup(), i.getGradeUid(),false);
            result.put(i.getGradeType(), count);
        }
        return result;
    }

    public List<UserDetailEntity> getMatchingUsers(UserDto userDto, boolean gender, boolean otherGender, int getN,
                                                   Map<String, Integer> countPercentageUserGrade){
        List<UserDetailEntity> getUserDetailEntity = new ArrayList<UserDetailEntity>();
        List<UserGradeEntity> listUserGradeEntity = userGradeRepository.findAllByDisabled(false);
        List<UserDetailEntity> returnUserDetailEntity = new ArrayList<UserDetailEntity>();
        Random random = new Random();
        int number1 = random.nextInt(getUserDetailEntity.size());
        int number2 = 0;

        do {
            number2 = random.nextInt(getUserDetailEntity.size());
        }while(number1 == number2);

        for(UserGradeEntity i : listUserGradeEntity){
            //Man
            if(gender){
                getUserDetailEntity.addAll(userDetailRepository.findDistinctByOtherM(userDto.getUserGroup(),
                        userDto.getUserGrade(), otherGender,
                        gender, false, false, userDto.getMinAge(), userDto.getMaxAge(),
                        LocalDateTime.now().minusMonths(1), userDto.getLatitude(), userDto.getLongitude(),
                        userDto.getMaxDistance(), countPercentageUserGrade.get(i.getGradeType())));
            }
            //Woman
            else{
                getUserDetailEntity.addAll(userDetailRepository.findDistinctByOtherW(userDto.getUserGroup(),
                        userDto.getUserGrade(), otherGender,
                        gender, false, false, userDto.getMinAge(), userDto.getMaxAge(),
                        LocalDateTime.now().minusMonths(1), userDto.getLatitude(), userDto.getLongitude(),
                        userDto.getMaxDistance(), countPercentageUserGrade.get(i.getGradeType())));
            }
            if(getN==1){
                returnUserDetailEntity.add(getUserDetailEntity.get(number1));
            }else{
                returnUserDetailEntity.add(getUserDetailEntity.get(number1));
                returnUserDetailEntity.add(getUserDetailEntity.get(number2));
            }

        }
        return returnUserDetailEntity;
    }

    public double moodDistanceSearch(UserDetailEntity userDetailEntity1, UserDetailEntity userDetailEntity2){
        int area = 0;
        double[][] userData1 =
                {
                        {userDetailEntity1.getRespect()*Math.cos(18), userDetailEntity1.getRespect()*Math.sin(18)},
                        {userDetailEntity1.getContact()*Math.cos(90), userDetailEntity1.getContact()*Math.sin(90)},
                        {userDetailEntity1.getDate()*Math.cos(168), userDetailEntity1.getDate()*Math.sin(168)},
                        {userDetailEntity1.getCommunication()*Math.cos(234), userDetailEntity1.getCommunication()*Math.sin(234)},
                        {userDetailEntity1.getSex()*Math.cos(306), userDetailEntity1.getSex()*Math.sin(306)}
                };
        double[][] userData2 =
                {
                        {userDetailEntity1.getRespect()*Math.cos(18), userDetailEntity1.getRespect()*Math.sin(18)},
                        {userDetailEntity1.getContact()*Math.cos(90), userDetailEntity1.getContact()*Math.sin(90)},
                        {userDetailEntity1.getDate()*Math.cos(168), userDetailEntity1.getDate()*Math.sin(168)},
                        {userDetailEntity1.getCommunication()*Math.cos(234), userDetailEntity1.getCommunication()*Math.sin(234)},
                        {userDetailEntity1.getSex()*Math.cos(306), userDetailEntity1.getSex()*Math.sin(306)}
                };

        return 0.0;



    }

    public void intersection(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        int px= (x1*y2 - y1*x2)*(x3-x4) - (x1-x2)*(x3*y4 - y3*x4);
        int py= (x1*y2 - y1*x2)*(y3-y4) - (y1-y2)*(x3*y4 - y3*x4);
        int p = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);

        if(p == 0) {
            System.out.println("parallel");
            return;
        }

        int x = px/p;
        int y = py/p;
        System.out.println(x + ", " + y);
    }

    //two point distance
    public double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "kilometer")
            dist = dist * 1.609344;
        else if(unit == "meter")
            dist = dist * 1609.344;
        return (dist);
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
