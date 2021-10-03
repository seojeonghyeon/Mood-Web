package com.mood.lockservice.controller;

import com.mood.lockservice.auth.AuthorizationExtractor;
import com.mood.lockservice.auth.BearerAuthConverser;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/")
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
        return String.format("It's Working in Lock Service"
                +", port(local.server.port)=" + env.getProperty("local.server.port")
                +", port(server.port)=" + env.getProperty("server.port")
                +", token secret=" + env.getProperty("token.secret")
                +", token expiration time=" + env.getProperty("token.expiration_time"));
    }

    @PostMapping("/addLockUser")
    public ResponseEntity<ResponseLockUser> addLockUser(HttpServletRequest request, @RequestBody RequestLockUser requestLockUser) {
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        boolean existUserUid = lockService.checkUserUid(userUid);
        if(existUserUid){
            if( (!requestLockUser.getLockUserUid().isEmpty()) && (!requestLockUser.getLockReasons().isEmpty()) &&
                    (!requestLockUser.getLockType().isEmpty()) && (!requestLockUser.getReferUid().isEmpty())){
                if(lockService.checkUserUid(requestLockUser.getLockUserUid())){
                    ModelMapper modelMapper = new ModelMapper();
                    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    LockUserDto lockUserDto = modelMapper.map(requestLockUser, LockUserDto.class);
                    lockUserDto.setFromUserUid(userUid);
                    lockUserDto.setActiveTime(LocalDateTime.now());
                    log.info(lockUserDto+" "+lockUserDto.getLockUserUid());
                    if(lockService.createLockUser(lockUserDto)){
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseLockUser());
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseLockUser());
    }

    @PostMapping("/{userUid}/updateLockUser")
    public ResponseEntity<ResponseLockUser> updateLockUser(@PathVariable("userUid")String userUid, @RequestBody RequestLockUser requestLockUser) {
        if(userUid.isEmpty() && requestLockUser.getLockType().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseLockUser());
        if(lockService.updateLockUser(userUid, requestLockUser.getLockType()))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseLockUser());
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
