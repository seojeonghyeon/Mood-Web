package com.mood.userservice.controller;

import com.mood.userservice.decode.DecodeUserToken;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.service.UserService;
import com.mood.userservice.vo.RequestUser;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/")
public class UserController {
    private Environment env;
    private UserService userService;
    private DecodeUserToken decodeUserToken;

    @Autowired
    public UserController(Environment env, UserService userService){
        this.env = env;
        this.userService = userService;
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

    @PostMapping("/testURL")
    public String statusRestfulAPI(HttpServletRequest request, HttpServletResponse response){
        String result = "userToken : "+request.getHeader("userToken")+" Request : ";
        result+=request;
        return result;
    }

    //미완성
    @PostMapping("/regist")
    public ResponseEntity createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        //

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    //미완성
    @PostMapping("resetPassword")
    public ResponseEntity resetPassword(@RequestHeader("userToken") String userToken,@RequestBody RequestUser requestUser){
        DecodeUserToken decodeUserToken = new DecodeUserToken();
        String userUid = decodeUserToken.getUserUidByUserToken(userToken, env);
        if(userUid.equals(null)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(requestUser);
        }
        if(requestUser.getPhoneNum().equals(null) || requestUser.getPassword().equals(null)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(requestUser);
        }
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return ResponseEntity.status(HttpStatus.OK).body(requestUser);
    }
}
