package com.mood.matchingservice.service;

import com.mood.matchingservice.dto.MatchingUserDto;
import com.mood.matchingservice.dto.UserDto;
import com.mood.matchingservice.jpa.*;
import com.mood.matchingservice.service.search.MatchingUsers;
import com.mood.matchingservice.service.search.MoodDistance;
import com.mood.matchingservice.vo.MatchingData;
import com.mood.matchingservice.vo.ResponseMatchingUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class MatchingServiceImpl implements MatchingService{
    private final static String VIP = "VIP";

    UserRepository userRepository;
    UserDetailRepository userDetailRepository;
    MatchingRepository matchingRepository;
    Environment env;
    UserGradeRepository userGradeRepository;

    @Autowired
    public MatchingServiceImpl(MatchingRepository matchingRepository, Environment env, UserRepository userRepository, UserDetailRepository userDetailRepository,
    UserGradeRepository userGradeRepository){
        this.env=env;
        this.matchingRepository=matchingRepository;
        this.userRepository=userRepository;
        this.userDetailRepository=userDetailRepository;
        this.userGradeRepository=userGradeRepository;
    }

    @Override
    public MatchingUserDto createMatchingUsers(MatchingUserDto matchingUser) {
        return null;
    }

    @Override
    public List<ResponseMatchingUser> getMatchingUsers(String userUid) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<ResponseMatchingUser> responseMatchingUserList = new ArrayList<>();
        MoodDistance moodDistance = new MoodDistance();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserUid(userUid);
        if(optionalUserEntity.isPresent()){
            Optional<UserDetailEntity> optionalUserDetailEntity = userDetailRepository.findByUserUid(optionalUserEntity.get().getUserUid());
            UserDto userDto = mapper.map(optionalUserDetailEntity.get(), UserDto.class);
            userDto.settingUserDto(optionalUserEntity.get());
            if(userDto.getMatchingTime().equals(userDto.getCreateTimeAt())){ // always main 2
                responseMatchingUserList = updateMatchingUsers(userDto, 1);
            }else if(!LocalDateTime.now().isBefore(userDto.getNextMatchingTime())){
                UserGradeEntity userGradeEntity = userGradeRepository.findByGradeType(VIP);
                if(userDto.getUserGrade().equals(userGradeEntity.getGradeUid())){
                    Random random = new Random();
                    int number = random.nextInt(3)+1;
                    log.info("number : "+number);
                    if(number==1){
                        responseMatchingUserList = updateMatchingUsers(userDto, 1);
                    }else if(number==2){
                        responseMatchingUserList = updateMatchingUsers(userDto, 2);
                    }else{
                        userDto = changeLocationAndSubLocation(userDto);
                        responseMatchingUserList = updateMatchingUsers(userDto, 1);
                        userDto = changeLocationAndSubLocation(userDto);
                    }
                }else{
                    responseMatchingUserList = updateMatchingUsers(userDto, 1);
                }
            }else{
                Optional<List<MatchingEntity>> optionalMatchingEntityList = matchingRepository.findByUserUidAndDisabled(userUid, false);
                if(optionalMatchingEntityList.isPresent()){
                    List<MatchingEntity> matchingEntityList = optionalMatchingEntityList.get();
                    for(MatchingEntity matchingEntity : matchingEntityList){
                        ResponseMatchingUser responseMatchingUser = null;
                        Optional<UserEntity> optionalUser = userRepository.findByUserUid(matchingEntity.getOtherUserUid());
                        Optional<UserDetailEntity> optionalUserDetail = userDetailRepository.findByUserUid(matchingEntity.getOtherUserUid());
                        UserDetailEntity userDetailEntity = optionalUserDetail.get();

                        UserDto otherDto = mapper.map(userDetailEntity, UserDto.class);
                        otherDto.settingUserDto(optionalUser.get());
                        responseMatchingUser = mapper.map(userDetailEntity, ResponseMatchingUser.class);
                        responseMatchingUser.setProfileImageIcon(otherDto.getProfileImageIcon());
                        responseMatchingUser.setProfileImage(otherDto.getProfileImage());
                        responseMatchingUser.setPhysicalDistance(distance(userDto.getLatitude(), userDto.getLongitude(),
                                    userDetailEntity.getLatitude(), userDetailEntity.getLongitude()));
                        responseMatchingUser.setNickname(otherDto.getNickname());

                        UserDto mathcingUserDto = mapper.map(responseMatchingUser, UserDto.class);
                        MatchingData matchingData = new MatchingData();
                        matchingData.setMoodDistance(moodDistance.search(userDto, mathcingUserDto));
                        matchingData.setMatchingTime(LocalDateTime.now());

                        responseMatchingUser.setMatchingData(matchingData);
                        responseMatchingUserList.add(responseMatchingUser);
                    }
                }
            }
            return responseMatchingUserList;
        }
        return null;
    }

    @Override
    public void updateMatchingUsers(MatchingUserDto matchingUserDto) {
    }

    @Override
    public List<ResponseMatchingUser> updateMatchingUsers(UserDto userDto, int number) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MoodDistance moodDistance = new MoodDistance();
        MatchingUsers matchingUser = new MatchingUsers(userRepository, userDetailRepository, userGradeRepository);

        //each user grade calculate percentage
        //matching
        Map<String, Integer> count = matchingUser.percentageCalculate(userDto);
        List<UserDetailEntity> matchingUsers = new ArrayList<>();
        List<MatchingEntity> matchingEntities = new ArrayList<>();

        if(number==1) {
            matchingUsers = matchingUser.getMatchingUsers(userDto, count, number, 0);
        }else {
            matchingUsers = matchingUser.getMatchingUsers(userDto, count, number, 1);
            userDto = changeLocationAndSubLocation(userDto);
            matchingUsers = matchingUser.getMatchingUsers(userDto, count, number, 2);
            userDto = changeLocationAndSubLocation(userDto);
        }

        //before data disabled true
        Optional<List<MatchingEntity>> optionalMatchingEntity = matchingRepository.findByUserUidAndDisabled(userDto.getUserUid(), false);
        if(optionalMatchingEntity.isPresent()){
            List<MatchingEntity> matchingEntityList = optionalMatchingEntity.get();
            for(MatchingEntity matchingEntity : matchingEntityList){
                Optional<MatchingEntity> optionalMatching = matchingRepository.findByMatchingIdAndUserUidAndDisabled(matchingEntity.getMatchingId(), matchingEntity.getUserUid(), false);
                optionalMatching.ifPresent(selectUser->{
                    selectUser.setDisabled(true);
                    matchingRepository.save(selectUser);
                });
            }
        }

        //new data save
        for(UserDetailEntity userDetailEntity : matchingUsers){
            Optional<UserEntity> optionalUserEntity = userRepository.findByUserUid(userDetailEntity.getUserUid());
            UserDto otherDto = mapper.map(userDetailEntity, UserDto.class);
            otherDto.settingUserDto(optionalUserEntity.get());

            MatchingEntity matchingEntity = new MatchingEntity();
            matchingEntity.setMatchingId(UUID.randomUUID().toString());
            matchingEntity.setMatchingTime(LocalDateTime.now());
            matchingEntity.setDisabled(false);
            matchingEntity.setUserUid(userDto.getUserUid());
            matchingEntity.setOtherUserUid(userDetailEntity.getUserUid());
            matchingEntity.setMoodDistance(moodDistance.search(userDto, otherDto));
            matchingRepository.save(matchingEntity);
            matchingEntities.add(matchingEntity);
        }

        //next Matching set
        Optional<UserEntity> optional = userRepository.findByUserUid(userDto.getUserUid());
        if(optional.get().getCreateTimeAt().isEqual(optional.get().getNextMatchingTime())) {
            optional.ifPresent(selectUser -> {
                selectUser.setMatchingTime(LocalDateTime.now());
                selectUser.setNextMatchingTime(LocalDateTime.now().plusHours(6));
                userRepository.save(selectUser);
            });
        }else{
            optional.ifPresent(selectUser -> {
                selectUser.setMatchingTime(selectUser.getNextMatchingTime());
                selectUser.setNextMatchingTime(selectUser.getNextMatchingTime().plusHours(6));
                userRepository.save(selectUser);
            });
        }


        //return value
        List<ResponseMatchingUser> matchingUserList = new ArrayList<ResponseMatchingUser>();
        ResponseMatchingUser responseMatchingUser = null;
        for(UserDetailEntity userDetailEntity : matchingUsers){
            Optional<UserEntity> optionalUserEntity = userRepository.findByUserUid(userDetailEntity.getUserUid());
            UserDto otherDto = mapper.map(userDetailEntity, UserDto.class);
            otherDto.settingUserDto(optionalUserEntity.get());

            responseMatchingUser = mapper.map(userDetailEntity, ResponseMatchingUser.class);
            responseMatchingUser.setProfileImageIcon(otherDto.getProfileImageIcon());
            responseMatchingUser.setProfileImage(otherDto.getProfileImage());
            responseMatchingUser.setPhysicalDistance(distance(userDto.getLatitude(), userDto.getLongitude(),
                        userDetailEntity.getLatitude(), userDetailEntity.getLongitude()));
            responseMatchingUser.setNickname(otherDto.getNickname());

            UserDto mathcingUserDto = mapper.map(responseMatchingUser, UserDto.class);
            MatchingData matchingData = new MatchingData();
            matchingData.setMoodDistance(moodDistance.search(userDto, mathcingUserDto));
            matchingData.setMatchingTime(LocalDateTime.now());

            responseMatchingUser.setMatchingData(matchingData);
            matchingUserList.add(responseMatchingUser);
        }
        return matchingUserList;
    }

    @Override
    public void updateMatchingTime(String userUid) {
        Optional<UserEntity> optional = userRepository.findByUserUid(userUid);
        optional.ifPresent(selectUser->{
            selectUser.setNextMatchingTime(LocalDateTime.now().plusMinutes(15));
            userRepository.save(selectUser);
        });
    }

    @Override
    public boolean updateResetMatchingTime(String userUid) {
        Optional<UserEntity> optional = userRepository.findByUserUid(userUid);
        if(optional.isPresent()) {
            UserEntity userEntity = optional.get();
            if(userEntity.isResetMatching()) {
                optional.ifPresent(selectUser -> {
                    selectUser.setResetMatching(false);
                    selectUser.setNextMatchingTime(LocalDateTime.now());
                    userRepository.save(selectUser);
                });
                return true;
            }
        }
        return false;
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
    private UserDto changeLocationAndSubLocation(UserDto userDto){
        String tempLocationENG = userDto.getLocationENG();
        String tempLocationKOR = userDto.getLocationKOR();
        double tempLatitude = userDto.getLatitude();
        double tempLongitude = userDto.getLongitude();

        userDto.setLocationKOR(userDto.getSubLocationKOR());
        userDto.setLocationENG(userDto.getSubLocationENG());
        userDto.setLatitude(userDto.getSubLatitude());
        userDto.setLongitude(userDto.getSubLongitude());

        userDto.setSubLocationKOR(tempLocationENG);
        userDto.setSubLocationENG(tempLocationKOR);
        userDto.setSubLatitude(tempLatitude);
        userDto.setSubLongitude(tempLongitude);

        return userDto;
    }
}
