package com.mood.userservice.controller;

import com.mood.userservice.decode.DecodeUserToken;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.service.UserService;
import com.mood.userservice.vo.RequestUser;
import com.mood.userservice.vo.ResponseUser;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user-service")
public class UserController {
    private Environment env;
    private UserService userService;


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

    @PostMapping("/regist")
    public ResponseEntity createUser(@RequestBody RequestUser user, HttpServletResponse response){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        //function, Matching Users

        String token = Jwts.builder()
                .setSubject(userDto.getUserUid())
                .setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();
        response.addHeader("userToken", token);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
    }

    @PostMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestHeader("userToken") String userToken,@RequestBody RequestUser requestUser){
        DecodeUserToken decodeUserToken = new DecodeUserToken();
        String userUid = decodeUserToken.getUserUidByUserToken(userToken, env);
        if(userUid.equals(null)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseUser());
        }
        if(requestUser.getPhoneNum().equals(null) || requestUser.getPassword().equals(null)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseUser());
        }
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //Service, Get User info from userUid, phoneNum
        //Set new password

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
    }

    @PostMapping("/checkPhoneNum")
    public ResponseEntity checkPhoneNumber(@RequestBody RequestUser requestUser){
        if(requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.getUserPhoneNumber(requestUser.getPhoneNum()))
            return ResponseEntity.status(HttpStatus.IM_USED).body(new ResponseUser());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
    }

//    @PostMapping("/findByEmail")
//    public ResponseEntity findByEmail(@RequestBody RequestUser requestUser){
//
//    }

//    @PostMapping("/certificateNumber")
//    public ResponseEntity certificateNumber(@RequestBody String numberId){
//
//    }

    @PostMapping("/findByPassword")
    public ResponseEntity findByPassword(@RequestBody RequestUser requestUser){
        if(requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(!userService.getUserPhoneNumber(requestUser.getPhoneNum()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseUser());
        userService.sendCreditNumber(requestUser.getPhoneNum());
        return ResponseEntity.status(HttpStatus.OK).body(new RequestUser());
    }
//    @PostMapping("/updateMatchingUsers")
//    public ResponseEntity updateMatchingUsers(@RequestBody RequestUser requestUser){
//
//    }
}
