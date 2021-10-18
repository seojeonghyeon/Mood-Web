package com.mood.matchingservice.controller;

import com.mood.matchingservice.auth.AuthorizationExtractor;
import com.mood.matchingservice.auth.BearerAuthConverser;
import com.mood.matchingservice.dto.MatchingUserDto;
import com.mood.matchingservice.dto.UserDto;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
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
    public ResponseEntity<List<ResponseMatchingUser>> getMatchingUsers(HttpServletRequest request){
        List<ResponseMatchingUser> returnList = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if(userUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(returnList);
        returnList = matchingService.getMatchingUsers(userUid);
        return ResponseEntity.status(HttpStatus.OK).body(returnList);
    }

    //get MatchingUser for Androids
    @PostMapping("/updateMatchingTime/{protoType}")
    public ResponseEntity<List<ResponseMatchingUser>> updateMatchingTime(HttpServletRequest request, @PathVariable String protoType) {
        List<ResponseMatchingUser> returnList = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if (userUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(returnList);
        if(protoType.equals("matching")||protoType.equals("profile")||protoType.equals("request")){
            matchingService.updateMatchingTime(userUid);
            return ResponseEntity.status(HttpStatus.OK).body(returnList);
        }else if(protoType.equals("reset")){
            if(matchingService.updateResetMatchingTime(userUid)) {
                returnList = matchingService.getMatchingUsers(userUid);
                return ResponseEntity.status(HttpStatus.OK).body(returnList);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(returnList);
    }
}
