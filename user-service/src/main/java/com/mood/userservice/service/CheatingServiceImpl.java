package com.mood.userservice.service;

import com.mood.userservice.jpa.UserEntity;
import com.mood.userservice.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CheatingServiceImpl implements CheatingService {

    UserRepository userRepository;

    @Autowired
    public CheatingServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public boolean openCheating(String userUid) {
        Optional<UserEntity> optional = userRepository.findByUserUid(userUid);
        if(optional.isPresent()){
            UserEntity userEntity = optional.get();
            if(userEntity.getCoin() >= 10){
                optional.ifPresent(selectUser->{
                    selectUser.setCoin(selectUser.getCoin()-10);
                    userRepository.save(selectUser);
                });
                return true;
            }
        }
        return false;
    }
}
