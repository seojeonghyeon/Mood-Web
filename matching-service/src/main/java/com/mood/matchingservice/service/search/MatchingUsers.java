package com.mood.matchingservice.service.search;

import com.mood.matchingservice.dto.UserDto;
import com.mood.matchingservice.jpa.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
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

    public List<UserDetailEntity> getMatchingUsers(UserDto userDto, Map<String, Integer> count, int number, int turnoff){
        List<UserDetailEntity> getMatchingUsers = new ArrayList<UserDetailEntity>();
        if(number==1){
            //Man want to man and woman
            if ((userDto.isGender()) && (userDto.isOtherM()) && (userDto.isOtherW())) {
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 1, count));
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 1, count));
            }
            //Woman want to man and woman
            else if ((!userDto.isGender()) && (userDto.isOtherM()) && (userDto.isOtherW())) {
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 1, count));
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 1, count));
            }
            //Man want to woman
            else if ((userDto.isGender()) && (!userDto.isOtherM()) && (userDto.isOtherW()))
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 2, count));
                //Man want to man
            else if ((userDto.isGender()) && (userDto.isOtherM()) && (!userDto.isOtherW()))
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 2, count));
                //Woman want to man
            else if ((!userDto.isGender()) && (userDto.isOtherM()) && (!userDto.isOtherW()))
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 2, count));
                //Woman want to woman
            else if ((!userDto.isGender()) && (!userDto.isOtherM()) && (userDto.isOtherW()))
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 2, count));
        }else{
            //Man want to man and woman
            if ((userDto.isGender()) && (userDto.isOtherM()) && (userDto.isOtherW())) {
                if(turnoff==1)
                    getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 1, count));
                else
                    getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 1, count));
            }
            //Woman want to man and woman
            else if ((!userDto.isGender()) && (userDto.isOtherM()) && (userDto.isOtherW())) {
                if(turnoff==1)
                    getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 1, count));
                else
                    getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 1, count));
            }
            //Man want to woman
            else if ((userDto.isGender()) && (!userDto.isOtherM()) && (userDto.isOtherW()))
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 1, count));
                //Man want to man
            else if ((userDto.isGender()) && (userDto.isOtherM()) && (!userDto.isOtherW()))
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 1, count));
                //Woman want to man
            else if ((!userDto.isGender()) && (userDto.isOtherM()) && (!userDto.isOtherW()))
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), true, 1, count));
                //Woman want to woman
            else if ((!userDto.isGender()) && (!userDto.isOtherM()) && (userDto.isOtherW()))
                getMatchingUsers.addAll(getMatchingUsers(userDto, userDto.isGender(), false, 1, count));
        }
        return getMatchingUsers;
    }

    public Map<String, Integer> percentageCalculate(UserDto userDto){
        List<UserGradeEntity> userGradeEntities = userGradeRepository.findAllByDisabled(false);
        Map<String, Integer> result = new HashMap<String,  Integer>();
        for(UserGradeEntity i : userGradeEntities){
            int count = userDetailRepository.countByUserGroupAndUserGradeAndDisabled(
                    userDto.getUserGroup(), i.getGradeUid(),false);
            log.info("Grade Count : "+count);
            long temp = (i.getGradePercent()*count)/100;
            int percentageCount = Math.round(temp);
            log.info("Percentage Count : "+percentageCount);
            result.put(i.getGradeType(), percentageCount);
        }
        return result;
    }

    public List<UserDetailEntity> getMatchingUsers(UserDto userDto, boolean gender, boolean otherGender, int getN,
                                                   Map<String, Integer> countPercentageUserGrade){
        List<UserDetailEntity> getUserDetailEntity = new ArrayList<>();
        List<UserGradeEntity> listUserGradeEntity = userGradeRepository.findAllByDisabled(false);
        List<UserDetailEntity> returnUserDetailEntity = new ArrayList<>();
        int totalSum = 0;

        Random random = new Random();

        for(UserGradeEntity i : listUserGradeEntity){
            if(countPercentageUserGrade.get(i.getGradeType()) > 0) {

                if (gender) { //Man
                    getUserDetailEntity.addAll(userDetailRepository.findByOtherM(userDto.getUserGroup(),
                            userDto.getUserGrade(), otherGender,
                            gender, false, false, userDto.getMinAge(), userDto.getMaxAge(),
                            LocalDateTime.now().minusMonths(1), userDto.getLatitude(), userDto.getLongitude(),
                            userDto.getMaxDistance(), userDto.getUserUid(), countPercentageUserGrade.get(i.getGradeType())));
                    log.info("Get Man Data : GradeType=" + i.getGradeType() + " Percentage=" + countPercentageUserGrade.get(i.getGradeType()));
                }else { //Woman
                    getUserDetailEntity.addAll(userDetailRepository.findByOtherW(userDto.getUserGroup(),
                            userDto.getUserGrade(), otherGender,
                            gender, false, false, userDto.getMinAge(), userDto.getMaxAge(),
                            LocalDateTime.now().minusMonths(1), userDto.getLatitude(), userDto.getLongitude(),
                            userDto.getMaxDistance(), userDto.getUserUid(), countPercentageUserGrade.get(i.getGradeType())));
                    log.info("Get Woman Data : GradeType=" + i.getGradeType() + " Percentage=" + countPercentageUserGrade.get(i.getGradeType()));
                }
                for(UserDetailEntity userDetailEntity : getUserDetailEntity) {
                    totalSum++;
                    log.info("Matching Other User : UserUid="+userDetailEntity.getUserUid() + " UserGender="+userDetailEntity.isGender());
                }
                log.info("Total Sum : "+totalSum);
                if(totalSum > 1) {
                    if (getN == 1) {
                        int number1 = random.nextInt(getUserDetailEntity.size());
                        returnUserDetailEntity.add(getUserDetailEntity.get(number1));
                    } else {
                        if (totalSum >= 2) {
                            int number1 = random.nextInt(totalSum);
                            int number2 = 0;
                            do {
                                number2 = random.nextInt(totalSum);
                            } while (number1 == number2);
                            returnUserDetailEntity.add(getUserDetailEntity.get(number1));
                            returnUserDetailEntity.add(getUserDetailEntity.get(number2));
                        } else {
                            int number1 = random.nextInt(totalSum);
                            returnUserDetailEntity.add(getUserDetailEntity.get(number1));
                        }
                    }
                }
            }
        }
        return returnUserDetailEntity;
    }
}
