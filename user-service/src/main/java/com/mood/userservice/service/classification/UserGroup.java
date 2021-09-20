package com.mood.userservice.service.classification;

import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.TotalUserRepository;
import com.mood.userservice.jpa.UserDetailRepository;
import com.mood.userservice.service.search.MoodDistance;
import org.springframework.beans.factory.annotation.Autowired;

public class UserGroup {

    MoodDistance moodDistance;
    UserDetailRepository userDetailRepository;
    TotalUserRepository totalUserRepository;

    public UserGroup(UserDetailRepository userDetailRepository, TotalUserRepository totalUserRepository){
        this.userDetailRepository=userDetailRepository;
        this.totalUserRepository=totalUserRepository;
    }

    public double selectDecisionTree(UserDto userDto){
        int totalUserCount = totalUserRepository.findTotalUserEntityByDisabledIsFalse().getTotaluser();
        if(totalUserCount < 500)
            return decisionTreeLevelOne(userDto);
        else if(totalUserCount < 1000)
            return decisionTreeLevelTwo(userDto);
        else if(totalUserCount < 2000)
            return decisionTreeLevelThree(userDto);
        else if(totalUserCount < 3000)
            return decisionTreeLevelFour(userDto);
        else
            return decisionTreeLevelFive(userDto);
    }

    public double decisionTreeLevelOne(UserDto userDto){
        return 1;
    }
    public double decisionTreeLevelTwo(UserDto userDto){
        double userMoodDistancePercentage =  moodDistance.findUserMoodDistanceOnTotal(userDto);
        //90% -> mean(4.5), 80% -> mean(4), 70% -> mean(3.5), 5+5+5+5+0=20 -> 4
        if(userMoodDistancePercentage > 80){
            return 2;
        }
        return 3;
    }
    public double decisionTreeLevelThree(UserDto userDto){
        int highCount = 0, lowCount=0;
        double userMoodDistancePercentage =  moodDistance.findUserMoodDistanceOnTotal(userDto);
        //90% -> mean(4.5), 80% -> mean(4), 70% -> mean(3.5), 5+5+5+5+0=20 -> 4
        if(userMoodDistancePercentage > 80){
            return 2;
        }else{
            highCount=(userDto.getRespect() >= 4 ? 1 : 0)+(userDto.getContact() >= 4 ? 1 : 0)
                    +(userDto.getDate() >= 4 ? 1 : 0)+(userDto.getCommunication() >= 4 ? 1 : 0)
                    +(userDto.getSex() >= 4 ? 1 : 0);
            lowCount=(userDto.getRespect() <= 1 ? 1 : 0)+(userDto.getContact() <= 1 ? 1 : 0)
                    +(userDto.getDate() <= 1 ? 1 : 0)+(userDto.getCommunication() <= 1 ? 1 : 0)
                    +(userDto.getSex() <= 1 ? 1 : 0);
            if(highCount >= 3){
                return 4;
            }else if(lowCount >= 3){
                return 5;
            }else if(highCount == 1){
                return 6;
            }else if(lowCount == 1){
                return 7;
            }
            return 3;
        }
    }
    public double decisionTreeLevelFour(UserDto userDto){
        int highCount = 0, lowCount=0;
        double userMoodDistancePercentage =  moodDistance.findUserMoodDistanceOnTotal(userDto);
        //90% -> mean(4.5), 80% -> mean(4), 70% -> mean(3.5), 5+5+5+5+0=20 -> 4
        if(userMoodDistancePercentage >= 80){
            return 2;
        }else{
            double[] checkPoint = new double[]{userDto.getRespect(), userDto.getContact(), userDto.getDate(),
                    userDto.getCommunication(), userDto.getSex()};
            int[] checkHighPoint = new int[5];
            int[] checkLowPoint = new int[5];
            for(int i=0; i<checkPoint.length; i++) {
                checkHighPoint[i] = (checkPoint[i] >= 4 ? (int)Math.pow(10,i) : 0);
                checkLowPoint[i] = (checkPoint[i] <= 1 ? (int)Math.pow(10,i) : 0);
                if (checkHighPoint[i] == 1) highCount++;
                if (checkLowPoint[i] == 1) lowCount++;
            }
            if(highCount >= 3){
                double sum = 0;
                for(int i=0; i<checkHighPoint.length; i++)
                    sum = sum + checkHighPoint[i];
                return sum;
            }else{
                if(userMoodDistancePercentage <= 20){
                    return 5;
                }else{
                    if(lowCount >= 3){
                        double sum = 0;
                        for(int i=0; i<checkHighPoint.length; i++)
                            sum = sum + checkHighPoint[i];
                        return sum;
                    }
                }
            }
        }
        return 6;
    }
    public double decisionTreeLevelFive(UserDto userDto){
        int highCount = 0, lowCount=0;
        double userMoodDistancePercentage =  moodDistance.findUserMoodDistanceOnTotal(userDto);
        //90% -> mean(4.5), 80% -> mean(4), 70% -> mean(3.5), 5+5+5+5+0=20 -> 4
        if(userMoodDistancePercentage >= 80){
            return 2;
        }else{
            double[] checkPoint = new double[]{userDto.getRespect(), userDto.getContact(), userDto.getDate(),
                    userDto.getCommunication(), userDto.getSex()};
            int[] checkHighPoint = new int[5];
            int[] checkLowPoint = new int[5];
            for(int i=0; i<checkPoint.length; i++) {
                checkHighPoint[i] = (checkPoint[i] >= 4 ? (int)Math.pow(10,i) : 0);
                checkLowPoint[i] = (checkPoint[i] <= 1 ? (int)Math.pow(10,i) : 0);
                if (checkHighPoint[i] == 1) highCount++;
                if (checkLowPoint[i] == 1) lowCount++;
            }
            if(highCount >= 3){
                double sum = 0;
                for(int i=0; i<checkHighPoint.length; i++)
                    sum = sum + checkHighPoint[i];
                return sum;
            }else{
                if(userMoodDistancePercentage <= 20){
                    return 5;
                }else{
                    if(lowCount >= 3){
                        double sum = 0;
                        for(int i=0; i<checkHighPoint.length; i++)
                            sum = sum + checkHighPoint[i];
                        return sum;
                    }else{
                        if(highCount == 1){
                            for(int i=0; i<checkHighPoint.length; i++)
                                if(checkHighPoint[i]!=0)
                                    return checkHighPoint[i];
                        }else if(lowCount == 1){
                            for(int i=0; i<checkHighPoint.length; i++)
                                if(checkHighPoint[i]!=0)
                                    return checkHighPoint[i];
                        }
                    }
                }
            }
        }
        return 7;
    }
}
