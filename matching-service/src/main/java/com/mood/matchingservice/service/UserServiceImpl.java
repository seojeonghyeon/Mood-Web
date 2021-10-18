package com.mood.matchingservice.service;

import com.mood.matchingservice.jpa.UserDetailRepository;
import com.mood.matchingservice.jpa.UserEntity;
import com.mood.matchingservice.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    UserDetailRepository userDetailRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDetailRepository userDetailRepository){
        this.userRepository=userRepository;
        this.userDetailRepository=userDetailRepository;
    }

    @Override
    public List<UserEntity> findByDisabled() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Iterable<UserEntity> iterable = userRepository.findByDisabled(false);
        List<UserEntity> list = new ArrayList<>();
        for(UserEntity userEntity : iterable)
            list.add(userEntity);
        return list;
    }
}
