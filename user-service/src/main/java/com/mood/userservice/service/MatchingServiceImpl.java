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

        //Matching Users, 2
        //Count percentage and get MatchingUsers
        Map<String, Integer> count = percentageCalculate(userDto);
        List<UserDetailEntity> matchingUsers = getMatchingUsers(userDto, count);
        List<RequestMatchingUser> matchingUserList = new ArrayList<RequestMatchingUser>();
        for(UserDetailEntity userDetailEntity : matchingUsers){
            UserEntity userEntity = userRepository.findByUserUid(userDetailEntity.getUserUid());
            RequestMatchingUser requestMatchingUser = mapper.map(userDetailEntity, RequestMatchingUser.class);
            requestMatchingUser.setProfileIcon(userEntity.getProfileImageIcon());
            requestMatchingUser.setProfileImage(userEntity.getProfileImage());
            requestMatchingUser.setPhysicalDistance(distance(userDto.getLatitude(),userDto.getLongitude(),
                    userDetailEntity.getLatitude(), userDetailEntity.getLongitude()));
            requestMatchingUser.setNickname(userEntity.getNickname());
            MatchingData matchingData = new MatchingData();
            matchingData.setMoodDistance(moodDistanceSearch(updateUserDetailEntity, userDetailEntity));
            matchingData.setMatchingTime(LocalDateTime.now());

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
        int[] degreeArray = new int[] {18, 90, 162, 234, 306, 18};
        int[] value1 = new int[]{userDetailEntity1.getRespect(), userDetailEntity1.getContact(),
                userDetailEntity1.getDate(), userDetailEntity1.getCommunication(), userDetailEntity1.getSex()};
        int[] value2 = new int[]{userDetailEntity2.getRespect(), userDetailEntity2.getContact(),
                userDetailEntity2.getDate(), userDetailEntity2.getCommunication(), userDetailEntity2.getSex()};
        ArrayList<double[]> userData1 = new ArrayList<double[]>();
        ArrayList<double[]> userData2 = new ArrayList<double[]>();
        ArrayList<double[]> intersections = new ArrayList<double[]>();
        ArrayList<double[]> unions = new ArrayList<double[]>();
        for(int i=0; i < degreeArray.length-1; i++){
            userData1.add(new double[]{value1[i]*Math.cos(deg2rad(degreeArray[i])), value1[i]*Math.sin(deg2rad(degreeArray[i]))});
            userData2.add(new double[]{value2[i]*Math.cos(deg2rad(degreeArray[i])), value2[i]*Math.sin(deg2rad(degreeArray[i]))});
        }

        for(int i = 0; i < degreeArray.length-1; i++){
            if(value1[i] > value2[i]){
                intersections.add(userData1.get(i));
                unions.add(userData2.get(i));
            }else if(value1[i] > value2[i]){
                intersections.add(userData2.get(i));
                unions.add(userData1.get(i));
            }else{
                intersections.add(userData2.get(i));
                unions.add(userData1.get(i));
            }
            double[] resultIntersection = intersection(userData1.get(i)[0], userData1.get(i)[1], userData1.get(i)[1], userData1.get(i)[1],
                    userData2.get(i)[0], userData2.get(i)[0], userData2.get(i)[1], userData2.get(i)[1],
                    degreeArray[i], degreeArray[i+1]);
            if(resultIntersection[0]!=0 && resultIntersection[1]!=0){
                intersections.add(resultIntersection);
                unions.add(resultIntersection);
            }
        }

        double [] intersectionsX = new double[intersections.size()];
        double [] intersectionsY = new double[intersections.size()];
        for (int i=0; i < intersections.size(); i++){
            intersectionsX[i] = intersections.get(i)[0];
            intersectionsY[i] = intersections.get(i)[1];
        }
        double intersectionArea = polygonArea(intersectionsX, intersectionsY, intersectionsX.length);
        double [] unionX = new double[unions.size()];
        double [] unionY = new double[unions.size()];
        for (int i=0; i < unions.size(); i++){
            unionX[i] = unions.get(i)[0];
            unionY[i] = unions.get(i)[1];
        }
        double unionArea = polygonArea(unionX,unionY,unionX.length);

        return intersectionArea/unionArea*100;
    }

    public double polygonArea(double[] X, double[] Y, int numPoints)
    {
        int area = 0;   // Accumulates area
        int j = numPoints-1;

        for (int i=0; i < numPoints; i++)
        { area +=  (X[j]+X[i]) * (Y[j]-Y[i]);
            j = i;  //j is previous vertex to i
        }
        return area/2;
    }

    public double[] intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, int degree1, int degree2) {

        int[] degreeArray = new int[] {18, 90, 162, 234, 306, 18};
        double px= (x1*y2 - y1*x2)*(x3-x4) - (x1-x2)*(x3*y4 - y3*x4);
        double py= (x1*y2 - y1*x2)*(y3-y4) - (y1-y2)*(x3*y4 - y3*x4);
        double p = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
        if(p == 0) {
            System.out.println("parallel");
            return new double[]{0 ,0};
        }
        double x = px/p;
        double y = py/p;

        for(int i=0; i < degreeArray.length-1; i++){
            if(-5 <= x && x <=-5 && -5 <= y && y <= 5){
                if((Math.tan(deg2rad(degreeArray[i])*x) < y) && (y < Math.tan(deg2rad(deg2rad(degreeArray[i+1])*x)))){
                    log.info("Intersection : X = "+x+" Y="+y);
                    return new double[]{x, y};
                }
            }
        }
        return new double[]{0 ,0};
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
