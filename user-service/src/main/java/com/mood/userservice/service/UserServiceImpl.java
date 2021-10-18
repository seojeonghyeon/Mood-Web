package com.mood.userservice.service;
import com.mood.userservice.client.LockServiceClient;
import com.mood.userservice.dto.CertificationNumberDto;
import com.mood.userservice.dto.PurchaseDto;
import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.*;
import com.mood.userservice.service.classification.UserGroup;
import com.mood.userservice.vo.RequestLockUser;
import com.mood.userservice.vo.ResponseLockUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final String VIP = "VIP";

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;
    Environment env;
    CircuitBreakerFactory circuitBreakerFactory;
    MessageService messageService;
    UserGradeRepository userGradeRepository;
    UserDetailRepository userDetailRepository;
    TotalUserRepository totalUserRepository;
    CertificationNumberRepository certificationNumberRepository;
    LockServiceClient lockServiceClient;
    UserGradeService userGradeService;
    RatePlanRepository ratePlanRepository;
    PurchaseRepository purchaseRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                           Environment env, CircuitBreakerFactory circuitBreakerFactory, MessageService messageService,
                           UserGradeRepository userGradeRepository, UserDetailRepository userDetailRepository,
                           TotalUserRepository totalUserRepository,
                           CertificationNumberRepository certificationNumberRepository, LockServiceClient lockServiceClient,
                            UserGradeService userGradeService, RatePlanRepository ratePlanRepository,
                           PurchaseRepository purchaseRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env=env;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.messageService=messageService;
        this.userGradeRepository=userGradeRepository;
        this.userDetailRepository=userDetailRepository;
        this.totalUserRepository=totalUserRepository;
        this.certificationNumberRepository= certificationNumberRepository;
        this.lockServiceClient=lockServiceClient;
        this.userGradeService=userGradeService;
        this.ratePlanRepository=ratePlanRepository;
        this.purchaseRepository=purchaseRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Before create User : "+LocalDateTime.now() + " = "+userDto);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        LocalDateTime localDateTime = LocalDateTime.now();
        userDto.setUserUid(UUID.randomUUID().toString());
        userDto.setCreateTimeAt(localDateTime);
        userDto.setRecentLoginTime(localDateTime);
        userDto.setMatchingTime(localDateTime);
        userDto.setNextMatchingTime(localDateTime);
        userDto.setUserAge(updateUserAge(userDto));

        //check circle grade
        userDto.setUserGrade(userGradeRepository.findByGradeType("newbie").getGradeUid());
        userDto.setGradeStart(localDateTime);
        userDto.setGradeEnd(localDateTime.plusDays(14));

        userDto.setCoin(0);
        userDto.setTicket(0);
        userDto.setDisabled(false);
        userDto.setUserLock(false);
        userDto.setCreditEnabled(false);
        userDto.setResetMatching(true);
        userDto.setCreditPwd("no pass");
        userDto.setMatchingTime(LocalDateTime.now());
        //set UserGroup
        UserGroup userGroup = new UserGroup(userDetailRepository, totalUserRepository);
        userDto.setUserGroup(userGroup.selectDecisionTree(userDto));
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        UserDetailEntity userDetailEntity = mapper.map(userDto, UserDetailEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPassword()));
        userDetailEntity.setOtherM(userDto.isOtherM());
        userDetailEntity.setOtherW(userDto.isOtherW());
        userDetailEntity.setLocationKOR(userDto.getLocationKOR());
        userDetailEntity.setLocationENG(userDto.getLocationENG());
        userDetailEntity.setSubLocationKOR(userDto.getSubLocationKOR());
        userDetailEntity.setSubLocationENG(userDto.getSubLocationENG());
        userRepository.save(userEntity);
        userDetailRepository.save(userDetailEntity);
        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);
        log.info("After create User : "+LocalDateTime.now() + " = "+userDto.getUserUid());
        return returnUserDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optional = userRepository.findByEmail(username);
        if(optional.isPresent()) {
            UserEntity userEntity = optional.get();
            return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), true, true, true, true,
                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        Optional<UserEntity> optional = userRepository.findByEmail(email);
        if(optional.isPresent()) {
            UserEntity userEntity = optional.get();
            Optional<UserDetailEntity> optionalUserDetailEntity = userDetailRepository.findByUserUid(userEntity.getUserUid());
            UserDetailEntity userDetailEntity = optionalUserDetailEntity.get();

            if(userEntity.isCreditEnabled()){
                userEntity.setCreditEnabled(false);
                userEntity.setCreditPwd("no pass");
            }

            optional.ifPresent(selectUser ->{
                LocalDate currentDate = LocalDate.now();
                LocalDate recentLoginTime = selectUser.getRecentLoginTime().toLocalDate();
                if(recentLoginTime.compareTo(currentDate) < 0)
                    selectUser.setLoginCount(userEntity.getLoginCount()+1);
                selectUser.setRecentLoginTime(LocalDateTime.now());
                userRepository.save(selectUser);
            });
            optionalUserDetailEntity.ifPresent(selectUser ->{
                selectUser.setRecentLoginTime(LocalDateTime.now());
                userDetailRepository.save(selectUser);
            });


            UserDto userDto = new ModelMapper().map(userDetailEntity, UserDto.class);
            userDto.settingUserDto(userEntity);
            return userDto;
        } else {
            throw new UsernameNotFoundException(email);
        }
    }

    @Override
    public boolean checkUserEmail(String email) {
        Optional<UserEntity> optional = userRepository.findByEmail(email);
        if(optional.isPresent()) return true;
        else return false;
    }

    @Override
    public String getEmailByPhoneNum(UserDto userDto) {
        Optional<UserEntity> optional = userRepository.findTop1ByCreditEnabledAndPhoneNumOrderByRecentLoginTime(false, userDto.getPhoneNum());
        if(optional.isPresent()) {
            UserEntity userEntity = optional.get();
            return userEntity.getEmail();
        }
        return "";
    }

    @Override
    public boolean getCertification(UserDto userDto) {
        Optional<UserEntity> optional = userRepository.findTop1ByPhoneNumOrderByRecentLoginTime(userDto.getPhoneNum());
        if(optional.isPresent()) {
            UserEntity userEntity = optional.get();
            if(LocalDateTime.now().isBefore(userEntity.getCreditTime().plusMinutes(30))){
                if(!userEntity.isCreditEnabled())
                    return true;
            }
        }
        return false;
    }
    @Override
    public void sendRegistCreditNumber(String phoneNum, String hashkey) {
        int number = messageService.createRandomNumber();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CertificationNumberEntity certificationNumberEntity;
        CertificationNumberDto certificationNumberDto = new CertificationNumberDto();
        Optional<CertificationNumberEntity> optional
                = certificationNumberRepository.findByPhoneNum(phoneNum);
        if(optional.isPresent()) {
            optional.ifPresent(selectUser->{
                selectUser.setPhoneNum(phoneNum);
                selectUser.setDisabled(false);
                selectUser.setCreatedAt(LocalDateTime.now());
                selectUser.setCreditNumber(number);
                certificationNumberRepository.save(selectUser);
            });
        }else{
            certificationNumberDto.setCertificationUid(UUID.randomUUID().toString());
            certificationNumberDto.setDisabled(false);
            certificationNumberDto.setCreatedAt(LocalDateTime.now());
            certificationNumberDto.setCreditNumber(number);
            certificationNumberDto.setPhoneNum(phoneNum);
            certificationNumberEntity = mapper.map(certificationNumberDto,
                    CertificationNumberEntity.class);
            certificationNumberRepository.save(certificationNumberEntity);
        }
        String message = " <#> [Mood] 본인인증 번호는 ["+number+"] 입니다. "+hashkey+"  ";
        messageService.sendMessage(message, phoneNum);
    }

    @Override
    public boolean checkRegistCertification(String phoneNum, String numberId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CertificationNumberEntity certificationNumberEntity;
        CertificationNumberDto certificationNumberDto;

        Optional<CertificationNumberEntity> optional
                = certificationNumberRepository.findByPhoneNumAndDisabled(phoneNum, false);
        if(optional.isPresent()) {
            certificationNumberEntity = optional.get();
            certificationNumberDto = mapper.map(certificationNumberEntity, CertificationNumberDto.class);
            if(LocalDateTime.now().isBefore(certificationNumberDto.getCreatedAt().plusMinutes(10))){
                if(certificationNumberDto.getCreditNumber()==Integer.parseInt(numberId)){
                    optional.ifPresent(selectUser ->{
                        selectUser.setDisabled(true);
                        certificationNumberRepository.save(selectUser);
                    });
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public boolean checkRegistCertificationIsTrue(String phoneNum) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CertificationNumberEntity certificationNumberEntity;

        Optional<CertificationNumberEntity> optional
                = certificationNumberRepository.findByPhoneNumAndDisabled(phoneNum, true);
        if (optional.isPresent()) {
            certificationNumberEntity = optional.get();
            if(LocalDateTime.now().isBefore(certificationNumberEntity.getCreatedAt().plusMinutes(30)))
                return true;
        }
        return false;
    }

    @Override
    public boolean updateUserLock(String userUid, boolean lockBoolean) {
        Optional<UserEntity> optional = userRepository.findByUserUid(userUid);
        optional.ifPresent(selectUser ->{
            selectUser.setUserLock(lockBoolean);
            userRepository.save(selectUser);
        });
        return true;
    }

    @Override
    public boolean checkCertification(String phoneNum, String numberId) {
        Optional<UserEntity> optional = userRepository.findTop1ByPhoneNumOrderByRecentLoginTime(phoneNum);
        if(optional.isPresent()) {
            UserEntity userEntity = optional.get();
            if(userEntity.getCreditNumber()==Integer.parseInt(numberId)) {
                optional.ifPresent(selectUser ->{
                            selectUser.setCreditEnabled(false);
                            selectUser.setCreditPwd("pass");
                            userRepository.save(selectUser);
                        });
                return true;
            }
        }
        return false;
    }

    @Override
    public String findByUserUid(String email, String phoneNum) {
        Optional<UserEntity> optional = userRepository.findByEmailAndPhoneNum(email, phoneNum);
        if(optional.isPresent()){
            UserEntity userEntity = optional.get();
            return userEntity.getUserUid();
        }
        return "";
    }

    @Override
    public void updateVIPCoin(UserEntity userEntity, int coin) {
        Optional<UserEntity> optional = userRepository.findByUserUid(userEntity.getUserUid());
        optional.ifPresent(selectUser ->{
            selectUser.setCoin(userEntity.getCoin()+coin);
            userRepository.save(selectUser);
        });
    }

    @Override
    public UserGradeEntity getVIPType() {
        return userGradeRepository.findByGradeType("VIP");
    }

    @Override
    public List<UserEntity> getUserGrade(UserGradeEntity userGradeEntity) {
        Optional<Iterable<UserEntity>> optional = userRepository.findByUserGrade(userGradeEntity.getGradeUid());
        if(optional.isPresent()){
            Iterable<UserEntity> iterable = optional.get();
            List<UserEntity> list = new ArrayList<>();
            iterable.forEach(v->
                    list.add(new ModelMapper().map(v, UserEntity.class)));
            return list;
        }
        return null;
    }

    @Override
    public String getGradeUid(String type) {
        UserGradeEntity userGradeEntity = userGradeRepository.findByGradeType(type);
        return userGradeEntity.getGradeUid();
    }

    @Override
    public String getGradeType(String uid) {
        UserGradeEntity userGradeEntity = userGradeRepository.findByGradeUid(uid);
        return userGradeEntity.getGradeType();
    }

    @Override
    public boolean sendCreditNumber(String phoneNum, String hashkey) {
        int randomNumber = messageService.createRandomNumber();
        String message = " <#> [Mood] 본인인증 번호는 ["+randomNumber+"] 입니다. "+hashkey+"  ";
        Optional<UserEntity> optional = userRepository.findTop1ByPhoneNumOrderByRecentLoginTime(phoneNum);
        if(optional.isPresent()) {
            optional.ifPresent(
                    selectUser->{
                        selectUser.setCreditNumber(randomNumber);
                        selectUser.setCreditEnabled(true);
                        selectUser.setCreditTime(LocalDateTime.now());
                        selectUser.setCreditPwd("no pass");
                        userRepository.save(selectUser);
                    }
            );
            messageService.sendMessage(message, phoneNum);
            return true;
        }
        return false;
    }

    @Override
    public boolean resetPassword(UserDto userDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Optional<UserEntity> optional = userRepository.findByUserUid(userDto.getUserUid());
        if(optional.isPresent()) {
            UserEntity getUserEntity = optional.get();
            UserDto getUserDto = modelMapper.map(getUserEntity, UserDto.class);
            if (userDto.getEmail().equals(getUserDto.getEmail()) && userDto.getPhoneNum().equals(getUserDto.getPhoneNum())) {
                getUserEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPassword()));
                userRepository.save(getUserEntity);
                return true;
            }
        }
        return false;
    }
    @Override
    public int updateUserAge(UserDto userDto){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthdate = LocalDate.parse(userDto.getBirthdate(),formatter);
        return LocalDateTime.now().getYear()-birthdate.getYear();
    }
    @Override
    public int updateUserAge(String birth){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthdate = LocalDate.parse(birth,formatter);
        return LocalDateTime.now().getYear()-birthdate.getYear();
    }

    @Override
    public List<UserDetailEntity> getByAll() {
        Iterable<UserDetailEntity> userDetailEntities = userDetailRepository.findAll();
        List<UserDetailEntity> list = new ArrayList<>();
        userDetailEntities.forEach(v->
                list.add(new ModelMapper().map(v, UserDetailEntity.class)));
        return list;
    }

    @Override
    public void updateUserAge(UserDetailEntity userDetailEntity) {
        Optional<UserDetailEntity> optional = userDetailRepository.findByUserUid(userDetailEntity.getUserUid());
        optional.ifPresent(selectUser->{
            selectUser.setUserAge(userDetailEntity.getUserAge());
            userDetailRepository.save(selectUser);
        });
    }

    @Override
    public void updateUserGrade(UserEntity userEntity, boolean vipDown) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserUid(userEntity.getUserUid());
        if(optionalUserEntity.isPresent()){
            Optional<UserDetailEntity> optionalUserDetailEntity = userDetailRepository.findByUserUid(userEntity.getUserUid());
            UserDetailEntity userDetailEntity = optionalUserDetailEntity.get();
            optionalUserEntity.ifPresent(selectUser->{
                selectUser.setUserGrade(userEntity.getUserGrade());
                selectUser.setGradeStart(userEntity.getGradeStart());
                selectUser.setGradeEnd(userEntity.getGradeEnd());
                selectUser.setLoginCount(userEntity.getLoginCount());
                userRepository.save(selectUser);
            });
            optionalUserDetailEntity.ifPresent(selectUser->{
                selectUser.setUserGrade(userEntity.getUserGrade());
                selectUser.setGradeStart(userEntity.getGradeStart());
                selectUser.setGradeEnd(userEntity.getGradeEnd());
                if(vipDown){
                    selectUser.setSubLocationKOR("");
                    selectUser.setSubLatitude(0.0);
                    selectUser.setSubLongitude(0.0);
                }
                userDetailRepository.save(selectUser);
            });
        }
    }

    @Override
    public UserDto updateUserGradeVIP(PurchaseDto purchaseDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserUid(purchaseDto.getUserUid());
        if(optionalUserEntity.isPresent()){
            Iterable<RatePlanEntity> ratePlanEntities = ratePlanRepository.findAll();
            List<RatePlanEntity> list = new ArrayList<>();
            ratePlanEntities.forEach(v->
                    list.add(new ModelMapper().map(v, RatePlanEntity.class)));
            for(RatePlanEntity ratePlanEntity : list){
                if(ratePlanEntity.getProductId().equals(purchaseDto.getProductId())){
                    optionalUserEntity.ifPresent(selectUser->{
                        selectUser.setUserGrade(userGradeService.getUserGrade(VIP));
                        selectUser.setGradeStart(LocalDateTime.now());
                        selectUser.setGradeEnd(LocalDateTime.now().plusMonths(ratePlanEntity.getMonths()));
                        userRepository.save(selectUser);
                    });
                    Optional<UserDetailEntity> optionalUserDetailEntity = userDetailRepository.findByUserUid(purchaseDto.getUserUid());
                    optionalUserDetailEntity.ifPresent(selectUser->{
                        selectUser.setUserGrade(userGradeService.getUserGrade(VIP));
                        selectUser.setGradeStart(LocalDateTime.now());
                        selectUser.setGradeEnd(LocalDateTime.now().plusMonths(ratePlanEntity.getMonths()));
                        userDetailRepository.save(selectUser);
                    });
                }
            }
        }
        PurchaseEntity purchaseEntity = modelMapper.map(purchaseDto, PurchaseEntity.class);
        purchaseEntity.setPurchaseUId(UUID.randomUUID().toString());
        purchaseRepository.save(purchaseEntity);
        return getUserDto(purchaseDto.getUserUid());
    }

    @Override
    public boolean updateUserSettings(UserDto userDto) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserUid(userDto.getUserUid());
        if(optionalUserEntity.isPresent()){
            UserEntity getUserEntity = optionalUserEntity.get();
            Optional<UserDetailEntity> optionalUserDetailEntity = userDetailRepository.findByUserUid(userDto.getUserUid());
            UserDetailEntity getUserDetailEntity = optionalUserDetailEntity.get();
            if(!getUserEntity.getPhoneNum().equals(userDto.getPhoneNum())){
                userDto.setPhoneNum(getUserEntity.getPhoneNum());
            }
            if(!getUserEntity.getNickname().equals(userDto.getNickname())){
                if(getUserEntity.getCoin() >= 10){
                    getUserEntity.setCoin(getUserEntity.getCoin()-10);
                }else{
                    userDto.setNickname(getUserEntity.getNickname());
                }
            }
            if(getUserDetailEntity.getMaxDistance()!=userDto.getMaxDistance()){
                if(getUserEntity.getCoin() >= 10){
                    getUserEntity.setCoin(getUserEntity.getCoin()-10);
                }else{
                    userDto.setMaxDistance(getUserDetailEntity.getMaxDistance());
                }
            }
            if(!getUserDetailEntity.getLocationENG().equals(userDto.getLocationENG())){
                if(getUserEntity.getCoin() >= 10){
                    getUserEntity.setCoin(getUserEntity.getCoin()-10);
                }else{
                    userDto.setLocationENG(getUserDetailEntity.getLocationENG());
                }
            }
            if(!getUserDetailEntity.getSubLocationENG().equals(userDto.getSubLocationENG())){
                if(!getUserEntity.getUserGrade().equals(userGradeService.getUserGrade(VIP))){
                    userDto.setSubLocationENG(getUserDetailEntity.getSubLocationENG());
                    userDto.setSubLatitude(getUserDetailEntity.getSubLatitude());
                    userDto.setSubLongitude(getUserDetailEntity.getSubLongitude());
                }
            }
            userDto.setCoin(getUserEntity.getCoin());
            optionalUserEntity.ifPresent(selectUser->{
                selectUser.setCoin(userDto.getCoin());
                selectUser.setNickname(userDto.getNickname());
                selectUser.setPhoneNum(userDto.getPhoneNum());
                selectUser.setProfileImage(userDto.getProfileImage());
                selectUser.setProfileImageIcon(userDto.getProfileImageIcon());
                userRepository.save(selectUser);
            });
            optionalUserDetailEntity.ifPresent(selectUser->{
                selectUser.setOtherW(userDto.isOtherW());
                selectUser.setOtherM(userDto.isOtherM());

                selectUser.setMaxDistance(userDto.getMaxDistance());
                selectUser.setMinAge(userDto.getMinAge());
                selectUser.setMaxAge(userDto.getMaxAge());

                selectUser.setLocationENG(userDto.getLocationENG());
                selectUser.setLatitude(userDto.getLatitude());
                selectUser.setLongitude(userDto.getLongitude());

                selectUser.setSubLocationENG(userDto.getSubLocationENG());
                selectUser.setSubLongitude(userDto.getSubLongitude());
                selectUser.setSubLatitude(userDto.getSubLatitude());
                userDetailRepository.save(selectUser);
            });
            return true;
        }
        return false;
    }

    @Override
    public UserDto getUser(String userUid) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserUid(userUid);
        Optional<UserDetailEntity> optionalUserDetailEntity = userDetailRepository.findByUserUid(userUid);
        if(optionalUserEntity.isPresent()){
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            UserEntity userEntity = optionalUserEntity.get();
            UserDetailEntity userDetailEntity = optionalUserDetailEntity.get();
            UserDto userDto = modelMapper.map(userDetailEntity, UserDto.class);
            userDto.settingUserDto(userEntity);
            userDto.setUserGrade(userGradeService.printUserGrade(userDto.getUserGrade()));
            return userDto;
        }
        return null;
    }

    @Override
    public UserDetailEntity getUserDetail(String userUid) {
        Optional<UserDetailEntity> optional = userDetailRepository.findByUserUid(userUid);
        if(optional.isPresent())
            return optional.get();
        return null;
    }

    @Override
    public boolean checkNickname(UserDto userDto) {
        Optional<UserEntity> optional = userRepository.findByNickname(userDto.getNickname());
        if(optional.isPresent())
            return true;
        return false;
    }

    @Override
    public boolean updatePhoneNum(String userUid, String phoneNum) {
        Optional<UserEntity> optional = userRepository.findByUserUid(userUid);
        if(optional.isPresent()){
            UserEntity userEntity = optional.get();
            if((userEntity.getCreditPwd().equals("pass")) && (LocalDateTime.now().isBefore(userEntity.getCreditTime().plusMinutes(10))) && (userEntity.getCoin()>=10)){
                optional.ifPresent(selectUser->{
                    selectUser.setCoin(selectUser.getCoin()-10);
                    selectUser.setPhoneNum(phoneNum);
                    userEntity.setCreditPwd("no pass");
                    userRepository.save(userEntity);
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public List<UserDto> getUsers() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Iterable<UserEntity> userEntities = userRepository.findByDisabled(false);
        List<UserDto> userDtoList = new ArrayList<>();
        for(UserEntity userEntity : userEntities){
            Optional<UserDetailEntity> optionalUserDetailEntity = userDetailRepository.findByUserUid(userEntity.getUserUid());
            UserDto userDto = modelMapper.map(optionalUserDetailEntity.get(), UserDto.class);
            userDto.settingUserDto(userEntity);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public void updateUserGroup(UserDto userDto) {
        UserGroup userGroup = new UserGroup(userDetailRepository, totalUserRepository);
        userDto.setUserGroup(userGroup.selectDecisionTree(userDto));
        Optional<UserDetailEntity> optional = userDetailRepository.findByUserUid(userDto.getUserUid());
        optional.ifPresent(selectUser->{
            selectUser.setUserGroup(userDto.getUserGroup());
            userDetailRepository.save(selectUser);
        });
    }

    @Override
    public UserDto getUserInfo(String userUid){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Optional<UserEntity> optional = userRepository.findByUserUid(userUid);
        if(optional.isPresent()) {
            UserEntity getUserEntity = optional.get();
            if(getUserEntity.isUserLock()){
                UserDto lockUserDto = new UserDto();
                lockUserDto.setUserUid(getUserEntity.getUserUid());
                lockUserDto.setEmail(getUserEntity.getEmail());
                lockUserDto.setUserLock(true);
                //신고사유 담기-> 신고서비스 구현 필요
                log.info("Before call lock microservice");
                CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
                RequestLockUser requestLockUser = new RequestLockUser();
                requestLockUser.setLockUserUid(getUserEntity.getUserUid());
                List<ResponseLockUser> lockUserList = circuitBreaker.run(()->lockServiceClient.getLockUser(requestLockUser),
                        throwable -> new ArrayList<>());
                log.info("After called lock microservice");
                String lockReason = "";
                for(ResponseLockUser getResponseLockUser : lockUserList){
                    lockReason = lockReason + getResponseLockUser.getLockType()+":"
                            +getResponseLockUser.getLockReasons()+"&";
                }
                lockUserDto.setUserLockReasons(lockReason);
                return lockUserDto;
            }
            if(getUserEntity.isDisabled()){
                UserDto disabledUserDto = new UserDto();
                disabledUserDto.setUserUid(getUserEntity.getUserUid());
                disabledUserDto.setEmail(getUserEntity.getEmail());
                disabledUserDto.setDisabled(true);
                return disabledUserDto;
            }
            if(getUserEntity.isCreditEnabled()){
                getUserEntity.setCreditEnabled(false);
                getUserEntity.setCreditPwd("no pass");
            }
            Optional<UserDetailEntity> optionalDetail = userDetailRepository.findByUserUid(userUid);
            UserDetailEntity userDetailEntity = optionalDetail.get();

            optional.ifPresent(selectUser ->{
                LocalDate currentDate = LocalDate.now();
                LocalDate recentLoginTime = selectUser.getRecentLoginTime().toLocalDate();
                if(recentLoginTime.compareTo(currentDate) < 0)
                    selectUser.setLoginCount(getUserEntity.getLoginCount()+1);
                selectUser.setRecentLoginTime(LocalDateTime.now());
                userRepository.save(selectUser);
            });
            optionalDetail.ifPresent(selectUser ->{
                selectUser.setRecentLoginTime(LocalDateTime.now());
                userDetailRepository.save(selectUser);
            });
            UserDto userDto = modelMapper.map(userDetailEntity, UserDto.class);
            userDto.settingUserDto(getUserEntity);
            userDto.setUserGrade(userGradeService.printUserGrade(userDto.getUserGrade()));
            return userDto;
        }
        return new UserDto();
    }

    @Override
    public boolean findByUserUid(String userUid) {
        Optional<UserEntity> optional = userRepository.findByUserUid(userUid);
        if(optional.isPresent()) return true;
        return false;
    }
    public UserDto getUserDto(String userUid) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Optional<UserEntity> optional = userRepository.findByUserUid(userUid);
        UserEntity userEntity = optional.get();
        userEntity.setUserGrade(userGradeService.printUserGrade(userEntity.getUserGrade()));
        if(optional.isPresent()) return modelMapper.map(optional.get(), UserDto.class);
        return null;
    }
}
