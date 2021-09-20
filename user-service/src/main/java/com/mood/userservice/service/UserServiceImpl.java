package com.mood.userservice.service;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.*;
import com.mood.userservice.service.classification.UserGroup;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;
    Environment env;
    CircuitBreakerFactory circuitBreakerFactory;
    MessageService messageService;
    UserGradeRepository userGradeRepository;
    UserDetailRepository userDetailRepository;
    TotalUserRepository totalUserRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                           Environment env, CircuitBreakerFactory circuitBreakerFactory, MessageService messageService,
                           UserGradeRepository userGradeRepository, UserDetailRepository userDetailRepository,
                           TotalUserRepository totalUserRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env=env;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.messageService=messageService;
        this.userGradeRepository=userGradeRepository;
        this.userDetailRepository=userDetailRepository;
        this.totalUserRepository=totalUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if(userEntity == null)
            throw new UsernameNotFoundException(username);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Before create User : "+LocalDateTime.now() + " = "+userDto.getUserUid());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        userDto.setUserUid(UUID.randomUUID().toString());
        userDto.setCreateTimeAt(LocalDateTime.now());
        userDto.setRecentLoginTime(LocalDateTime.now());
        userDto.setGradeStart(LocalDateTime.now());
        userDto.setUserAge(updateUserAge(userDto));

        //check circle grade
        userDto.setUserGrade(userGradeRepository.findByGradeType("newbie").getGradeUid());
        userDto.setGradeStart(LocalDateTime.now());
        userDto.setGradeEnd(LocalDateTime.now().plusDays(14));

        userDto.setCoin(0);
        userDto.setTicket(0);
        userDto.setDisabled(false);
        userDto.setUserLock(false);
        userDto.setCreditEnabled(false);
        userDto.setResetMatching(true);

        //set UserGroup
        UserGroup userGroup = new UserGroup(userDetailRepository, totalUserRepository);
        userDto.setUserGroup(userGroup.selectDecisionTree(userDto));
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        UserDetailEntity userDetailEntity = mapper.map(userDto, UserDetailEntity.class);

        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(userEntity);
        userDetailRepository.save(userDetailEntity);
        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        log.info("After create User : "+LocalDateTime.now() + " = "+userDto.getUserUid());
        return returnUserDto;
    }

    @Override
    public UserDto getUserDetailsByEmail(String userName) {
        return null;
    }

    @Override
    public boolean getUserPhoneNumber(String phoneNum) {
        UserEntity userEntity = userRepository.findByPhoneNum(phoneNum);
        if(userEntity.equals(null)) return false;
        return true;
    }

    @Override
    public void sendCreditNumber(String phoneNum) {
        int randomNumber = messageService.createRandomNumber();
        String message = "[Mood] 본인인증 번호는 ["+randomNumber+"] 입니다.";
        messageService.sendMessage(message, phoneNum);
        UserEntity userEntity = userRepository.findByPhoneNum(phoneNum);
        userEntity.setCreditNumber(randomNumber);
        userRepository.save(userEntity);
    }

    public boolean updateMatchingUsers(UserDto userDto){

        return false;
    }
    public int updateUserAge(UserDto userDto){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime birthdate = LocalDateTime.parse(userDto.getBirthdate(),formatter);
        return LocalDateTime.now().getYear()-birthdate.getYear();
    }
}
