package com.mood.userservice.controller;

import com.mood.userservice.decode.DecodeUserToken;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.dto.UserGradeDto;
import com.mood.userservice.service.MatchingService;
import com.mood.userservice.service.UserGradeService;
import com.mood.userservice.service.UserService;
import com.mood.userservice.vo.RequestUser;
import com.mood.userservice.vo.RequestUserGrade;
import com.mood.userservice.vo.ResponseLockUser;
import com.mood.userservice.vo.ResponseUser;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
@Slf4j
@RestController
@RequestMapping("/")
public class UserController {
    private Environment env;
    private UserService userService;
    private MatchingService matchingService;
    private UserGradeService userGradeService;


    @Autowired
    public UserController(Environment env, UserService userService, UserGradeService userGradeService, MatchingService matchingService){
        this.env = env;
        this.userService = userService;
        this.matchingService = matchingService;
        this.userGradeService = userGradeService;
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

    //##
    @PostMapping("/sendRegistCertification")
    public ResponseEntity sendRegistCertification(@RequestBody RequestUser requestUser){
        if(requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userService.sendRegistCreditNumber(userDto.getPhoneNum(), requestUser.getHashkey());
        return ResponseEntity.status(HttpStatus.OK).body(new RequestUser());
    }

    //##
    @PostMapping("/checkRegistCertification")
    public ResponseEntity checkRegistCertification(@RequestBody RequestUser requestUser){
        if(requestUser.getNumberId().isEmpty() || requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.checkRegistCertification(requestUser.getPhoneNum(), requestUser.getNumberId()))
            return ResponseEntity.status(HttpStatus.OK).body(new RequestUser());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    //##
    @PostMapping("/regist")
    public ResponseEntity createUser(@RequestBody RequestUser user, HttpServletResponse response){
        if(user.getEmail().isEmpty()&& user.getPassword().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(!userService.checkRegistCertificationIsTrue(user.getPhoneNum())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        }
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        userDto = userService.createUser(userDto);
        matchingService.updateMatchingUsers(userDto);
        String token = Jwts.builder()
                .setSubject(userDto.getUserUid())
                .setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();
        response.addHeader("userToken", token);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
    }


    @PostMapping("/checkEmail")
    public ResponseEntity checkEmail(@RequestBody RequestUser requestUser){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        if(requestUser.getEmail().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.checkUserEmail(userDto))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
    }

    @PostMapping("/findByEmail")
    public ResponseEntity findByEmail(@RequestBody RequestUser requestUser){
        if(requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        if(userService.getCertification(userDto)){
            ResponseUser responseUser = new ResponseUser();
            responseUser.setEmail(userService.getEmailByPhoneNum(userDto));
            return ResponseEntity.status(HttpStatus.OK).body(responseUser);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    @PostMapping("/findByPassword")
    public ResponseEntity findByPassword(@RequestBody RequestUser requestUser){
        if(requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        if(requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(!userService.checkUserPhoneNumber(userDto))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseUser());
        userService.sendCreditNumber(requestUser.getPhoneNum(), requestUser.getHashkey());
        return ResponseEntity.status(HttpStatus.OK).body(new RequestUser());
    }

    @PostMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestHeader("userToken") String userToken, @RequestBody RequestUser requestUser){
        DecodeUserToken decodeUserToken = new DecodeUserToken();
        String userUid = decodeUserToken.getUserUidByUserToken(userToken, env);
        if(userUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseUser());
        if(requestUser.getPhoneNum().equals(null) || requestUser.getPassword().equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseUser());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userDto.setUserUid(userUid);
        if(userService.resetPassword(userDto))  return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    @PostMapping("/autologin")
    public ResponseEntity autoLogin(@RequestHeader("userToken") String userToken , HttpServletResponse response){
        DecodeUserToken decodeUserToken = new DecodeUserToken();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        String userUid = decodeUserToken.getUserUidByUserToken(userToken, env);
        if(userUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseUser());
        UserDto userDto = new UserDto();
        userDto.setUserUid(userUid);
        userDto = userService.getUserInfo(userDto);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        if(userDto.getUserUid().isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        String token = Jwts.builder()
                .setSubject(userDto.getUserUid())
                .setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();
        response.addHeader("userToken", token);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    @PostMapping("/usergrade/regist")
    public ResponseEntity createUserGrade(@RequestBody RequestUserGrade userGrade){
        if(userGrade.getGradeType().isEmpty() || userGrade.getGradePercent().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserGradeDto userGradeDto = modelMapper.map(userGrade, UserGradeDto.class);
        if(userGradeService.createUserGrade(userGradeDto)){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    @PostMapping("/usergrade/disabled")
    public ResponseEntity disabledUserGrade(@RequestBody RequestUserGrade userGrade){
        if(userGrade.getGradeType().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserGradeDto userGradeDto = modelMapper.map(userGrade, UserGradeDto.class);
        if(userGradeService.disabledUserGrade(userGradeDto)){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    @GetMapping("/userUid/exist/{userUid}")
    public ResponseEntity<ResponseLockUser> existUserUid(@PathVariable String userUid){
        ResponseLockUser responseLockUser = new ResponseLockUser();
        if(userService.findByUserUid(userUid)){
            responseLockUser.setExist(true);
            return ResponseEntity.status(HttpStatus.OK).body(responseLockUser);
        }else{
            responseLockUser.setExist(false);
            return ResponseEntity.status(HttpStatus.OK).body(responseLockUser);
        }
    }

    @GetMapping("/userUid/updateLockUser/{userUid}")
    public ResponseEntity<ResponseLockUser> updateLockUserUid(@PathVariable String userUid){
        ResponseLockUser responseLockUser = new ResponseLockUser();
        if(userService.updateUserLock(userUid)){
            responseLockUser.setExist(true);
            return ResponseEntity.status(HttpStatus.OK).body(responseLockUser);
        }else{
            responseLockUser.setExist(false);
            return ResponseEntity.status(HttpStatus.OK).body(responseLockUser);
        }
    }

}
