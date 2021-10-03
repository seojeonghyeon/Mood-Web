package com.mood.lockservice.service;

import com.mood.lockservice.client.UserServiceClient;
import com.mood.lockservice.dto.LockUserDto;
import com.mood.lockservice.jpa.LockUserEntity;
import com.mood.lockservice.jpa.LockUserRepository;
import com.mood.lockservice.vo.ResponseLockUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class LockServiceImpl implements LockService{

    Environment env;
    CircuitBreakerFactory circuitBreakerFactory;
    UserServiceClient userServiceClient;
    LockUserRepository lockUserRepository;

    @Autowired
    public LockServiceImpl(Environment env, CircuitBreakerFactory circuitBreakerFactory,
                           UserServiceClient userServiceClient, LockUserRepository lockUserRepository){
        this.env=env;
        this.circuitBreakerFactory=circuitBreakerFactory;
        this.userServiceClient=userServiceClient;
        this.lockUserRepository = lockUserRepository;
    }

    @Override
    public boolean createLockUser(LockUserDto lockUserDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        lockUserDto.setLockUid(UUID.randomUUID().toString());

        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        ResponseLockUser responseLockUser = circuitBreaker.run(()->
                userServiceClient.updateLockUser(lockUserDto.getLockUserUid(),true));
        log.info("After called users microservice");

        LockUserEntity lockUserEntity = mapper.map(lockUserDto, LockUserEntity.class);
        lockUserRepository.save(lockUserEntity);
        return true;
    }

    @Override
    public Iterable<LockUserEntity> getLockUser(String lockUserUid) {
        return lockUserRepository.findByLockUserUid(lockUserUid);
    }

    @Override
    public boolean updateLockUser(String userUid, String lockType) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Optional<LockUserEntity> optional
                = lockUserRepository.findByLockUserUidAndLockType(userUid, lockType);
        if(optional.isPresent()){
            Iterable<LockUserEntity> lockUserEntities = lockUserRepository.findByLockUserUid(userUid);
            List<ResponseLockUser> result = new ArrayList<>();
            lockUserEntities.forEach(v->
                    result.add(new ModelMapper().map(v, ResponseLockUser.class)));
            optional.ifPresent(selectUser ->{
                selectUser.setLockUserDisabled(false);
                lockUserRepository.save(selectUser);
            });
            if(result.size()<=1){
                log.info("Before call users microservice");
                CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
                ResponseLockUser responseLockUser = circuitBreaker.run(()->
                        userServiceClient.updateLockUser(userUid,false));
                log.info("After called users microservice");
            }
        }
        return true;
    }

    @Override
    public boolean checkUserUid(String userUid) {
        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        ResponseLockUser responseLockUser = circuitBreaker.run(()->userServiceClient.checkUserUid(userUid));
        log.info("After called users microservice");
        if(responseLockUser.isExist())
            return true;
        else
            return false;
    }
}
