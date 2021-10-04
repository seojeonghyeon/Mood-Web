package com.mood.userservice.service;

import com.mood.userservice.dto.UserGradeDto;
import com.mood.userservice.jpa.UserEntity;
import com.mood.userservice.jpa.UserGradeEntity;
import com.mood.userservice.jpa.UserGradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserGradeServiceImpl implements UserGradeService{

    UserGradeRepository userGradeRepository;
    Environment env;

    @Autowired
    public void UserServiceImpl(Environment env, UserGradeRepository userGradeRepository){
        this.userGradeRepository=userGradeRepository;
        this.env=env;
    }
    @Override
    public boolean createUserGrade(UserGradeDto userGradeDto) {
        userGradeDto.setGradeUid(UUID.randomUUID().toString());
        userGradeDto.setDisabled(false);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserGradeEntity userGradeEntity = mapper.map(userGradeDto, UserGradeEntity.class);
        userGradeRepository.save(userGradeEntity);
        return true;
    }

    @Override
    public boolean disabledUserGrade(UserGradeDto userGradeDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserGradeEntity userGradeEntity = userGradeRepository.findByGradeType(userGradeDto.getGradeType());
        if(userGradeEntity.equals(null))
            return false;
        userGradeEntity.setDisabled(true);
        userGradeRepository.save(userGradeEntity);
        return true;
    }

    @Override
    public List<UserGradeEntity> getUserGrade() {
        Iterable<UserGradeEntity> iterable = userGradeRepository.findAll();
        List<UserGradeEntity> list = new ArrayList<>();
        iterable.forEach(v->
                list.add(new ModelMapper().map(v, UserGradeEntity.class)));
        return list;
    }
}
