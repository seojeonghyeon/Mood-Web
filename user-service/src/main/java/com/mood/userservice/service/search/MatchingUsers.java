package com.mood.userservice.service.search;

import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

public class MatchingUsers {

    UserRepository userRepository;
    UserDetailRepository userDetailRepository;
    UserGradeRepository userGradeRepository;

    @Autowired
    public MatchingUsers(UserRepository userRepository, UserDetailRepository userDetailRepository, UserGradeRepository userGradeRepository){
        this.userRepository=userRepository;
        this.userDetailRepository=userDetailRepository;
        this.userGradeRepository=userGradeRepository;
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
}
