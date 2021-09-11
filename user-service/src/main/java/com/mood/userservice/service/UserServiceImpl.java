package com.mood.userservice.service;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.UserDetailEntity;
import com.mood.userservice.jpa.UserEntity;
import com.mood.userservice.jpa.UserRepository;
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

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                           Environment env, CircuitBreakerFactory circuitBreakerFactory){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env=env;
        this.circuitBreakerFactory = circuitBreakerFactory;
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

        userDto.setUserUid(UUID.randomUUID().toString());
        userDto.setCreateTimeAt(LocalDateTime.now());
        userDto.setRecentLoginTime(LocalDateTime.now());
        userDto.setGradeStart(LocalDateTime.now());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        UserDetailEntity userDetailEntity = mapper.map(userDto, UserDetailEntity.class);

        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(userEntity);
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
        String message = "[Mood] 본인인증 번호는"+createRandomNumber()+"입니다.";
        sendMessage(message, phoneNum);
    }
    public String createRandomNumber(){
        Random random = new Random();
        String number = " ["+(random.nextInt(8888)+1111)+"] ";
        return number;
    }

    public void sendMessage(String message, String toNumber){
        //Confirm the function, Environment wtf..
        log.info("Before send message : "+LocalDateTime.now()+" = To : "+toNumber+" Message : "+message);
        String apiKey="";                              //env.getProperty("messaging.apiKey");
        String apiSecret="";           //env.getProperty("messaging.apiSecret");
        String fromNumber= "";                              //env.getProperty("messaging.fromNumber");

        Message coolsms = new Message(apiKey, apiSecret);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", toNumber);
        params.put("from", fromNumber);
        params.put("type", "SMS");
        params.put("text", message);
        params.put("app_version", "test app 0.5"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
        log.info("After send message");
    }
}
