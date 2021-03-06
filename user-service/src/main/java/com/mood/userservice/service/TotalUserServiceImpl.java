package com.mood.userservice.service;

import com.mood.userservice.dto.TotalUserDto;
import com.mood.userservice.jpa.TotalUserEntity;
import com.mood.userservice.jpa.TotalUserRepository;
import com.mood.userservice.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class TotalUserServiceImpl implements TotalUserService{

    TotalUserRepository totalUserRepository;
    UserRepository userRepository;
    public TotalUserServiceImpl(TotalUserRepository totalUserRepository, UserRepository userRepository){
        this.totalUserRepository=totalUserRepository;
        this.userRepository=userRepository;
    }

    @Override
    public TotalUserDto checkTotalUser() {
        int totaluser = userRepository.countByDisabledIsFalse();
        TotalUserDto totalUserDto = new TotalUserDto();
        totalUserDto.setTotaluser(totaluser);
        totalUserDto.setDisabled(false);
        totalUserDto.setCreatedAt(LocalDateTime.now());

        TotalUserEntity beforeTotalUserEntity = totalUserRepository.findTotalUserEntityByDisabled(false);
        beforeTotalUserEntity.setDisabled(true);
        totalUserRepository.save(beforeTotalUserEntity);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        TotalUserEntity totalUserEntity = mapper.map(totalUserDto, TotalUserEntity.class);
        totalUserRepository.save(totalUserEntity);
        return null;
    }
}
