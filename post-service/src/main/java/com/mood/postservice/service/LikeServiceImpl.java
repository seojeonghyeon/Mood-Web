package com.mood.postservice.service;

import com.mood.postservice.dto.LikeDto;
import com.mood.postservice.jpa.LikeEntity;
import com.mood.postservice.jpa.LikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService{

    LikeRepository likeRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository){
        this.likeRepository=likeRepository;
    }

    @Override
    public boolean updateLike(LikeDto likeDto) {
        boolean HOCK = true;
        boolean check = true;
        Optional<LikeEntity> optional = likeRepository.findByLikeUidAndPostIdAndCommentId(likeDto.getLikeUid(),likeDto.getPostId(),likeDto.getCommentId());
        if(optional.isPresent()) {
            HOCK = false;
            if(optional.get().isDisabled()==check)
                check=false;
            else
                check=true;
        }
        optional.ifPresent(selectUser->{
            selectUser.setDisabled(!selectUser.isDisabled());
            selectUser.setLikeTime(LocalDateTime.now());
            likeRepository.save(selectUser);
        });
        if(HOCK){
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            likeDto.setLikeId(UUID.randomUUID().toString());
            likeDto.setLikeTime(LocalDateTime.now());
            likeDto.setDisabled(false);
            LikeEntity likeEntity = modelMapper.map(likeDto, LikeEntity.class);
            likeRepository.save(likeEntity);
            check=false;
        }
        return check;
    }
}
