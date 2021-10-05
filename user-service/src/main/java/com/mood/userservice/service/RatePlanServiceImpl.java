package com.mood.userservice.service;

import com.mood.userservice.dto.RatePlanDto;
import com.mood.userservice.jpa.RatePlanEntity;
import com.mood.userservice.jpa.RatePlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RatePlanServiceImpl implements RatePlanService{

    RatePlanRepository ratePlanRepository;

    @Autowired
    public RatePlanServiceImpl(RatePlanRepository ratePlanRepository){
        this.ratePlanRepository=ratePlanRepository;
    }

    @Override
    public void addRatePlan(RatePlanDto ratePlanDto) {
        Optional<RatePlanEntity> optional = ratePlanRepository.findByProductId(ratePlanDto.getProductId());
        if(optional.isPresent()){
            optional.ifPresent(selectUser->{
                selectUser.setDisabled(ratePlanDto.isDisabled());
                selectUser.setMonths(ratePlanDto.getMonths());
                selectUser.setProductId(ratePlanDto.getProductId());
                selectUser.setRateplanType(ratePlanDto.getRateplanType());
                ratePlanRepository.save(selectUser);
            });
        }else{
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            RatePlanEntity ratePlanEntity = modelMapper.map(ratePlanDto, RatePlanEntity.class);
            ratePlanEntity.setRateplanId(UUID.randomUUID().toString());
            ratePlanRepository.save(ratePlanEntity);
        }
    }

    @Override
    public RatePlanDto getRatePlan(String productId) {
        Optional<RatePlanEntity> optional = ratePlanRepository.findByProductId(productId);
        if(optional.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(optional.get(), RatePlanDto.class);
        }
        return null;
    }

    @Override
    public Iterable<RatePlanEntity> getRatePlans() {
        return ratePlanRepository.findAll();
    }
}
