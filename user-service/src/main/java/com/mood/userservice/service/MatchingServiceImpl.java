package com.mood.userservice.service;

import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class MatchingServiceImpl {
    UserRepository userRepository;
    UserDetailRepository userDetailRepository;
    UserGradeRepository userGradeRepository;
    BlockUserRepository blockUserRepository;
    Environment env;
    CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public MatchingServiceImpl(UserRepository userRepository, UserDetailRepository userDetailRepository,
                           UserGradeRepository userGradeRepository,BlockUserRepository blockUserRepository, Environment env, CircuitBreakerFactory circuitBreakerFactory){
        this.userRepository = userRepository;
        this.userDetailRepository= userDetailRepository;
        this.userGradeRepository = userGradeRepository;
        this.blockUserRepository = blockUserRepository;
        this.env=env;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public boolean updateMatchingUsers(UserDto userDto){
        List<UserDetailEntity> userDetailEntity = new ArrayList<>();
        //Man, want to woman
        if(userDto.isGender() && !userDto.isOtherM() &&  userDto.isOtherW())
            userDetailEntity=getMatchingUsers(false,  true,  userDto,  2);
            //Man, want to man
        else if(userDto.isGender() && userDto.isOtherM() &&  !userDto.isOtherW())
            userDetailEntity=getMatchingUsers(true,  true,  userDto,  2);
            //Man, want to woman, man
        else if(userDto.isGender() && userDto.isOtherM() &&  !userDto.isOtherW()){
            int Ncheck=1;
            userDetailEntity=getMatchingUsers(true, true, userDto, 1);
            if(userDetailEntity.isEmpty())
                Ncheck=2;
            if(Ncheck==2)
                userDetailEntity=getMatchingUsers(false, true, userDto, Ncheck);
            else
                userDetailEntity.add(getMatchingUsers(false, true, userDto, Ncheck).get(0));
        }
        //Woman, want to woman
        else if(!userDto.isGender() && !userDto.isOtherM() &&  userDto.isOtherW())
            userDetailEntity=getMatchingUsers(false,  false,  userDto,  2);
            //Woman, want to man
        else if(!userDto.isGender() && userDto.isOtherM() &&  !userDto.isOtherW())
            userDetailEntity=getMatchingUsers(true,  false,  userDto,  2);
            //Woman, want to woman, man
        else if(!userDto.isGender() && userDto.isOtherM() &&  userDto.isOtherW()){
            int Ncheck=1;
            userDetailEntity=getMatchingUsers(false, false, userDto, 1);
            if(userDetailEntity.isEmpty())
                Ncheck=2;
            if(Ncheck==2)
                userDetailEntity=getMatchingUsers(true, false, userDto, Ncheck);
            else
                userDetailEntity.add(getMatchingUsers(true, false, userDto, Ncheck).get(0));
        }else{
            log.info("Matching is Fail : no want anyone");
            return false;
        }
        return false;
    }


    public List<UserDetailEntity> getMatchingUsers(boolean gender, boolean otherGender, UserDto userDto, int N){
        int number1 = 0;
        int number2 = 0;
        Random random = new Random();
        ModelMapper mapper = new ModelMapper();
        List<BlockUserEntity> blockUserEntities = blockUserRepository.findDistinctByUserUidAndDisabled(userDto.getUserUid(),false);
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDetailEntity userDetailEntity = mapper.map(userDto, UserDetailEntity.class);
        List<UserDetailEntity> getUserDetailEntity;
        String newbie = userGradeRepository.findByGradeType("newbie").getGradeUid();
        String basic = userGradeRepository.findByGradeType("basic").getGradeUid();
        String heavy = userGradeRepository.findByGradeType("heavy").getGradeUid();
        String VIP = userGradeRepository.findByGradeType("VIP").getGradeUid();

        //man
        if(otherGender){
            getUserDetailEntity = userDetailRepository
                    .findTop20ByUserGroupAndGenderAndOtherMAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime
                            (userDetailEntity.getUserGroup(), gender, true,
                                    userDetailEntity.getMaxDistance(), newbie, false,
                                    userDetailEntity.getRecentLoginTime(), userDetailEntity.getMinAge(),
                                    userDetailEntity.getMaxAge());
            getUserDetailEntity.addAll(userDetailRepository
                    .findTop25ByUserGroupAndGenderAndOtherMAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime
                            (userDetailEntity.getUserGroup(), gender, true,
                                    userDetailEntity.getMaxDistance(), basic,false,
                                    userDetailEntity.getRecentLoginTime(), userDetailEntity.getMinAge(),
                                    userDetailEntity.getMaxAge()));
            getUserDetailEntity.addAll(userDetailRepository
                    .findTop25ByUserGroupAndGenderAndOtherMAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime
                            (userDetailEntity.getUserGroup(), gender, true,
                                    userDetailEntity.getMaxDistance(), heavy,false,
                                    userDetailEntity.getRecentLoginTime(), userDetailEntity.getMinAge(),
                                    userDetailEntity.getMaxAge()));
            getUserDetailEntity.addAll(userDetailRepository
                    .findTop30ByUserGroupAndGenderAndOtherMAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime
                            (userDetailEntity.getUserGroup(), gender, true,
                                    userDetailEntity.getMaxDistance(), VIP,false,
                                    userDetailEntity.getRecentLoginTime(), userDetailEntity.getMinAge(),
                                    userDetailEntity.getMaxAge()));
        }
        //woman
        else{
            getUserDetailEntity = userDetailRepository
                    .findTop20ByUserGroupAndGenderAndOtherWAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime
                            (userDetailEntity.getUserGroup(), gender, true,
                                    userDetailEntity.getMaxDistance(), newbie,false,
                                    userDetailEntity.getRecentLoginTime(), userDetailEntity.getMinAge(),
                                    userDetailEntity.getMaxAge());
            getUserDetailEntity.addAll(userDetailRepository
                    .findTop25ByUserGroupAndGenderAndOtherWAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime
                            (userDetailEntity.getUserGroup(), gender, true,
                                    userDetailEntity.getMaxDistance(), basic,false,
                                    userDetailEntity.getRecentLoginTime(), userDetailEntity.getMinAge(),
                                    userDetailEntity.getMaxAge()));
            getUserDetailEntity.addAll(userDetailRepository
                    .findTop25ByUserGroupAndGenderAndOtherWAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime
                            (userDetailEntity.getUserGroup(), gender, true,
                                    userDetailEntity.getMaxDistance(), heavy,false,
                                    userDetailEntity.getRecentLoginTime(), userDetailEntity.getMinAge(),
                                    userDetailEntity.getMaxAge()));
            getUserDetailEntity.addAll(userDetailRepository
                    .findTop30ByUserGroupAndGenderAndOtherWAndMaxDistanceAndUserGradeAndDisabledAndRecentLoginTimeGreaterThanEqualAndUserAgeBetweenOrderByRecentLoginTime
                            (userDetailEntity.getUserGroup(), gender, true,
                                    userDetailEntity.getMaxDistance(), VIP,false,
                                    userDetailEntity.getRecentLoginTime(), userDetailEntity.getMinAge(),
                                    userDetailEntity.getMaxAge()));
        }
        for(UserDetailEntity i : getUserDetailEntity)
            for(BlockUserEntity j : blockUserEntities)
                if(i.getPhoneNum().equals(j.getBlockPhoneNum()))
                    getUserDetailEntity.remove(i);
        List<UserDetailEntity> returnUserDetailEntity = new ArrayList<>();
        if(N==1 && getUserDetailEntity.size() >= 2){
            number1 = (random.nextInt(getUserDetailEntity.size()));
            returnUserDetailEntity.add(getUserDetailEntity.get(number1));
        }else if(N==2 && getUserDetailEntity.size() >= 2){
            do{
                number1 = (random.nextInt(getUserDetailEntity.size()));
                number2 = (random.nextInt(getUserDetailEntity.size()));
            }while(number1 == number2);
            returnUserDetailEntity.add(getUserDetailEntity.get(number1));
            returnUserDetailEntity.add(getUserDetailEntity.get(number2));
        }else{
            log.info("Can not do matching service : matching data is lower than 2");
            return null;
        }
        return returnUserDetailEntity;
    }
}
