package com.mood.matchingservice.controller;

import com.mood.matchingservice.decode.DecodeUserToken;
import com.mood.matchingservice.dto.MatchingUserDto;
import com.mood.matchingservice.service.MatchingService;
import com.mood.matchingservice.vo.RequestUser;
import com.mood.matchingservice.vo.ResponseMatchingUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matching-service")
public class MatchingController {
    private Environment env;
    private MatchingService matchingService;

    @Autowired
    public MatchingController(Environment env, MatchingService matchingService){
        this.env=env;
        this.matchingService = matchingService;
    }

    //Back-end Server, User Service Health Check
    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service"
                +", port(local.server.port)=" + env.getProperty("local.server.port")
                +", port(server.port)=" + env.getProperty("server.port")
                +", token secret=" + env.getProperty("token.secret")
                +", token expiration time=" + env.getProperty("token.expiration_time"));
    }

    //End matching and update matching-service
    @PostMapping("updateMatchingUsers")
    public ResponseEntity updateMatchingUsers(@RequestBody RequestUser requestUser){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MatchingUserDto matchingUserDto = mapper.map(requestUser, MatchingUserDto.class);
        matchingService.updateMatchingUsers(matchingUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMatchingUser());
    }

    //get MatchingUser for Androids
    @PostMapping("/getMatchingUsers")
    public ResponseEntity getMatchingUsers(@RequestHeader String userToken){
        DecodeUserToken decodeUserToken = new DecodeUserToken();
        String userUid = decodeUserToken.getUserUidByUserToken(userToken, env);
        if(userUid.equals(null)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMatchingUser());
        }
        MatchingUserDto matchingUserDto = new MatchingUserDto();
        matchingUserDto.setUserUid(userUid);
        //get matching users
        matchingService.getMatchingUsers();
        //get user-service and get Matching user's data

        //return matching users
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMatchingUser());
    }

    //get MatchingUSer for Server
    @GetMapping("/getMatchingUsers")
    public ResponseEntity getMatchingUsers(@RequestBody RequestUser requestUser){


        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMatchingUser());
    }
}
