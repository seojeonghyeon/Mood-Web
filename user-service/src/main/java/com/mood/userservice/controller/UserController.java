package com.mood.userservice.controller;

import com.mood.userservice.auth.AuthorizationExtractor;
import com.mood.userservice.auth.BearerAuthConverser;
import com.mood.userservice.auth.CreateAuthBearerToken;
import com.mood.userservice.dto.*;
import com.mood.userservice.jpa.RatePlanEntity;
import com.mood.userservice.service.*;
import com.mood.userservice.vo.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/")
public class UserController {
    private Environment env;
    private UserService userService;
    private MatchingService matchingService;
    private UserGradeService userGradeService;
    private RatePlanService ratePlanService;
    private BlockUserService blockUserService;


    @Autowired
    public UserController(Environment env, UserService userService, UserGradeService userGradeService, MatchingService matchingService,
                          RatePlanService ratePlanService, BlockUserService blockUserService){
        this.env = env;
        this.userService = userService;
        this.matchingService = matchingService;
        this.userGradeService = userGradeService;
        this.ratePlanService = ratePlanService;
        this.blockUserService = blockUserService;
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
    public ResponseEntity<ResponseUser> sendRegistCertification(@RequestBody RequestUser requestUser){
        if(requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userService.sendRegistCreditNumber(userDto.getPhoneNum(), requestUser.getHashkey());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
    }

    //##
    @PostMapping("/checkRegistCertification")
    public ResponseEntity<ResponseUser> checkRegistCertification(@RequestBody RequestUser requestUser){
        if(requestUser.getNumberId().isEmpty() || requestUser.getPhoneNum().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.checkRegistCertification(requestUser.getPhoneNum(), requestUser.getNumberId()))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    //##
    @PostMapping("/regist")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user, HttpServletResponse response){
        if(user.getEmail().isEmpty()&& user.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        }
        if(!userService.checkRegistCertificationIsTrue(user.getPhoneNum())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        }
        if(userService.checkUserEmail(user.getEmail()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        userDto.setLocationKOR(user.getLocationKOR());
        userDto.setSubLocationKOR(user.getSubLocationKOR());
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

    //##
    @PostMapping("/checkEmail")
    public ResponseEntity<ResponseUser> checkEmail(@RequestBody RequestUser requestUser){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        log.info(userDto.getEmail());
        if(requestUser.getEmail().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.checkUserEmail(userDto.getEmail()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
    }

    @PostMapping("/checkNickname")
    public ResponseEntity<ResponseUser> checkNickname(@RequestBody RequestUser requestUser){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        log.info(userDto.getEmail());
        if(requestUser.getNickname().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.checkNickname(userDto))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
    }

    //##
    @PostMapping("/sendCertification")
    public ResponseEntity<ResponseUser> sendCertification(@RequestBody RequestUser requestUser){
        if(requestUser.getPhoneNum().isEmpty() || requestUser.getHashkey().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.sendCreditNumber(requestUser.getPhoneNum(), requestUser.getHashkey()))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    //##
    @PostMapping("/certificateNumber")
    public ResponseEntity<ResponseUser> certificateNumber(@RequestBody RequestUser requestUser){
        if(requestUser.getPhoneNum().isEmpty() || requestUser.getNumberId().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.checkCertification(requestUser.getPhoneNum(), requestUser.getNumberId()))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }


    //##
    @PostMapping("/findByEmail")
    public ResponseEntity<ResponseUser> findByEmail(@RequestBody RequestUser requestUser){
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

    //##
    @PostMapping("/findByPassword")
    public ResponseEntity<ResponseUser> findByPassword(@RequestBody RequestUser requestUser, HttpServletResponse response){
        if(requestUser.getPhoneNum().isEmpty() || requestUser.getEmail().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        if(userService.getCertification(userDto)){
            String token = new CreateAuthBearerToken().createToken(env, userService.findByUserUid(requestUser.getEmail(),
                    requestUser.getPhoneNum()));
            response.addHeader("userToken", token);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseUser());
    }

    //##
    @PostMapping("/resetPassword")
    public ResponseEntity<ResponseUser> resetPassword(HttpServletRequest request, @RequestBody RequestUser requestUser){
        if(requestUser.getEmail().isEmpty() || requestUser.getPhoneNum().isEmpty() || requestUser.getPassword().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if(userUid.equals(null))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userDto.setUserUid(userUid);
        if(userService.resetPassword(userDto))  return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    //##
    @PostMapping("/autologin")
    public ResponseEntity<ResponseUser> autoLogin(HttpServletRequest request, HttpServletResponse response){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if(userUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseUser());
        UserDto userDto = new UserDto();
        userDto.setUserUid(userUid);
        userDto = userService.getUserInfo(userUid);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        if(!userDto.getUserGrade().equals("VIP")){
            responseUser.setSubLocationKOR("");
            responseUser.setSubLocationENG("");
        }

        String token = new CreateAuthBearerToken().createToken(env, userUid);
        response.addHeader("userToken", token);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    @PostMapping("/getUser")
    public ResponseEntity<ResponseUser> getUser(HttpServletRequest request, @RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if (userUid.equals(null))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(userService.findByUserUid(userUid)){
            if(!requestUser.getUserUid().isEmpty()) {
                UserDto userDto = new UserDto();
                userDto.setUserUid(requestUser.getUserUid());
                userDto = userService.getUserInfo(userUid);
                ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
                if(!userDto.getUserGrade().equals("VIP")){
                    responseUser.setSubLocationKOR("");
                    responseUser.setSubLocationENG("");
                }
                return ResponseEntity.status(HttpStatus.OK).body(responseUser);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    @PostMapping("/purchaseVIP")
    public ResponseEntity<ResponseUser> purchaseVIP(HttpServletRequest request, @RequestBody RequestPurchase requestPurchase) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if (userUid.equals(null))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        if(requestPurchase.isAcknowledged() && (requestPurchase.getPurchaseState()==0)){
            log.info("" + requestPurchase);
            PurchaseDto purchaseDto = mapper.map(requestPurchase, PurchaseDto.class);
            purchaseDto.setUserUid(userUid);
            UserDto userDto = userService.updateUserGradeVIP(purchaseDto);
            ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

            if(!userDto.getUserGrade().equals("VIP")){
                responseUser.setSubLocationKOR("");
                responseUser.setSubLocationENG("");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseUser);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    @PostMapping("/setMatching")
    public ResponseEntity<ResponseUser> setMatching(HttpServletRequest request, @RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if (userUid.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userDto.setUserUid(userUid);
        log.info(""+userDto);
        if(userService.updateUserSettings(userDto)) {
            userDto = userService.getUser(userDto.getUserUid());
            ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
            if(!userDto.getUserGrade().equals("VIP")){
                responseUser.setSubLocationKOR("");
                responseUser.setSubLocationENG("");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseUser);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    @PostMapping("/blockPhoneNums")
    public ResponseEntity<ResponseUser> blockPhoneNums(HttpServletRequest request, @RequestBody List<String> phoneNumList) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if (userUid.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
        List<BlockUserDto> blockUserDtoList = new ArrayList<>();
        for(String phoneNum : phoneNumList){
            BlockUserDto blockUserDto = new BlockUserDto();
            blockUserDto.setUserUid(userUid);
            blockUserDto.setPhoneNum(phoneNum);
            blockUserDtoList.add(blockUserDto);
        }
        if(blockUserService.updateBlockUsers(userUid, blockUserDtoList))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseUser());
    }

    @PostMapping("/getBlockPhoneNums")
    public ResponseEntity<List<String>> getBlockPhoneNums(HttpServletRequest request, @RequestBody List<String> phoneNumList) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        if (userUid.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
        List<String> list = blockUserService.getBlockUsers(userUid);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    //##
    @PostMapping("/usergrade/regist")
    public ResponseEntity<ResponseUser> createUserGrade(@RequestBody RequestUserGrade userGrade){
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
    public ResponseEntity<ResponseUser> disabledUserGrade(@RequestBody RequestUserGrade userGrade){
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

    //##
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

    //##
    @GetMapping("/userUid/updateLockUser/{userUid}/{lockBoolean}")
    public ResponseEntity<ResponseLockUser> updateLockUserUid(@PathVariable String userUid, @PathVariable boolean lockBoolean){
        ResponseLockUser responseLockUser = new ResponseLockUser();
        if(userService.updateUserLock(userUid, lockBoolean)){
            responseLockUser.setExist(true);
            return ResponseEntity.status(HttpStatus.OK).body(responseLockUser);
        }else{
            responseLockUser.setExist(false);
            return ResponseEntity.status(HttpStatus.OK).body(responseLockUser);
        }
    }

    @PostMapping("/rateplans/addRatePlan")
    public ResponseEntity<ResponseRatePlan> addRatePlan(@RequestBody RequestRatePlan requestRatePlan){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RatePlanDto ratePlanDto = mapper.map(requestRatePlan, RatePlanDto.class);
        ratePlanService.addRatePlan(ratePlanDto);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseRatePlan());
    }

    @PostMapping("/rateplans/getRatePlan")
    public ResponseEntity<ResponseRatePlan> getRatePlan(@RequestBody RequestRatePlan requestRatePlan){
        if(requestRatePlan.getProductId().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseRatePlan());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RatePlanDto ratePlanDto = ratePlanService.getRatePlan(requestRatePlan.getProductId());
        ResponseRatePlan responseRatePlan = mapper.map(ratePlanDto, ResponseRatePlan.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseRatePlan);
    }

    @PostMapping("/rateplans/addRatePlans")
    public ResponseEntity<List<ResponseRatePlan>> getRatePlans(){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Iterable<RatePlanEntity> ratePlanEntities = ratePlanService.getRatePlans();
        List<ResponseRatePlan> list = new ArrayList<>();
        ratePlanEntities.forEach(v->
                list.add(new ModelMapper().map(v, ResponseRatePlan.class)));
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
