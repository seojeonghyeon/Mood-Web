package com.mood.lockservice.controller;

import com.mood.lockservice.decode.DecodeUserToken;
import com.mood.lockservice.dto.LockUserDto;
import com.mood.lockservice.jpa.LockUserEntity;
import com.mood.lockservice.service.LockService;
import com.mood.lockservice.vo.RequestLockUser;
import com.mood.lockservice.vo.ResponseLockUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/lock-service")
public class LockController {
    private Environment env;
    private LockService lockService;

    @Autowired
    public LockController(Environment env, LockService lockService){
        this.env = env;
        this.lockService=lockService;
    }
    //Back-end Server, Lock Service Health Check
    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service"
                +", port(local.server.port)=" + env.getProperty("local.server.port")
                +", port(server.port)=" + env.getProperty("server.port")
                +", token secret=" + env.getProperty("token.secret")
                +", token expiration time=" + env.getProperty("token.expiration_time"));
    }

    @PostMapping("/addLockUser")
    public ResponseEntity addLockUser(@RequestHeader("userToken") String userToken, @RequestBody RequestLockUser requestLockUser) {
        DecodeUserToken decodeUserToken = new DecodeUserToken();
        String userUid = decodeUserToken.getUserUidByUserToken(userToken, env);
        if(lockService.checkUserUid(userUid)){
            if( (!requestLockUser.getLockUserUid().isEmpty()) && (!requestLockUser.getLockReasons().isEmpty()) &&
                    (!requestLockUser.getLockType().isEmpty()) && (!requestLockUser.getReferUid().isEmpty())){
                if(lockService.checkUserUid(requestLockUser.getLockUserUid())){
                    ModelMapper modelMapper = new ModelMapper();
                    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    LockUserDto lockUserDto = modelMapper.map(requestLockUser, LockUserDto.class);
                    lockUserDto.setFromUserUid(userUid);
                    lockUserDto.setActiveTime(LocalDateTime.now());
                    if(lockService.createLockUser(lockUserDto)){
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseLockUser());
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseLockUser());
    }

    @PostMapping("/getLockUser")
    public ResponseEntity<List<ResponseLockUser>> getLockUser(@RequestBody RequestLockUser requestLockUser) {
        log.info("Before retrieve locks data");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Iterable<LockUserEntity> lockUserEntities = lockService.getLockUser(requestLockUser.getLockUserUid());
        List<ResponseLockUser> result = new ArrayList<>();
        lockUserEntities.forEach(v->
                result.add(new ModelMapper().map(v, ResponseLockUser.class)));
        log.info("Add retrieve locks data");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
