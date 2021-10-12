package com.mood.postservice.service;

import com.mood.postservice.dto.HashtagDto;
import com.mood.postservice.dto.PostDto;
import com.mood.postservice.jpa.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PostServiceImpl implements PostService{

    final static private int DEFAULT_PAGE_SIZE = 100;
    final static private String VIP = "VIP";

    Environment env;
    PostRepository postRepository;
    HashtagRepository hashtagRepository;
    UserRepository userRepository;
    UserDetailRepository userDetailRepository;
    UserGradeRepository userGradeRepository;

    @Autowired
    public PostServiceImpl(Environment env, PostRepository postRepository, HashtagRepository hashtagRepository, UserRepository userRepository,
                           UserDetailRepository userDetailRepository, UserGradeRepository userGradeRepository){
        this.env=env;
        this.postRepository=postRepository;
        this.hashtagRepository=hashtagRepository;
        this.userRepository=userRepository;
        this.userDetailRepository=userDetailRepository;
        this.userGradeRepository=userGradeRepository;
    }

    @Override
    public boolean registPost(PostDto postDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<HashtagEntity> hashtagEntityList = new ArrayList<>();

        postDto.setPostId(UUID.randomUUID().toString());
        postDto.setPostTime(LocalDateTime.now());
        postDto.setPostCommentCount(0);
        postDto.setPostLikeCount(0);
        postDto.setDisabled(false);
        for(HashtagDto hashtagDto : postDto.getHashtagDtos()){
            hashtagDto.setHashTagId(UUID.randomUUID().toString());
            hashtagDto.setPostId(postDto.getPostId());
            hashtagDto.setPostUid(postDto.getPostUid());
            hashtagDto.setHashingTime(LocalDateTime.now());
            hashtagDto.setDisabled(false);

            hashtagEntityList.add(modelMapper.map(hashtagDto, HashtagEntity.class));
        }

        PostEntity postEntity = modelMapper.map(postDto, PostEntity.class);
        postRepository.save(postEntity);
        for (HashtagEntity hashtagEntity : hashtagEntityList)
            hashtagRepository.save(hashtagEntity);

        return true;
    }

    @Override
    public boolean checkUserUid(String postUid) {
        Optional<UserEntity> optional = userRepository.findByUserUid(postUid);
        if(optional.isPresent())
            return true;
        return false;
    }

    @Override
    public List<PostDto> getPostByType(String postUid, String postType, int page) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<PostDto> postDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE);

        UserEntity userEntity = getUserEntityByPostDto(postUid);
        UserGradeEntity userGradeEntity = getUserGradeEntityByGradeUid(userEntity.getUserGrade());
        log.info("Post Type : " + postType+" page : "+page);
        int number = switch(postType){
            case "total" -> 1;
            case "popular" -> 2;
            case "areaA" -> 3;
            case "areaB" -> 4;
        };
        if((!userGradeEntity.getGradeType().equals(VIP)) && (number==4))
            number = 3;


        if(number == 1){
            Iterable<PostEntity> postEntities = postRepository.findByDisabledOrderByPostTimeDesc(false, pageRequest);
            for(PostEntity postEntity : postEntities)
                postDtoList.add(modelMapper.map(postEntity, PostDto.class));
        }else if(number == 2){
            Iterable<PostEntity> postEntities = postRepository.findByDisabledOrderByPostLikeCountDesc(false, pageRequest);
            for(PostEntity postEntity : postEntities)
                postDtoList.add(modelMapper.map(postEntity, PostDto.class));
        }else if(number == 3){
            UserDetailEntity userDetailEntity = getUserDetailEntityByPostDto(postUid);
            Iterable<PostEntity> postEntities = postRepository.findByDisabledAndLocationENGOrderByPostTimeDesc(false, userDetailEntity.getLocationENG(), pageRequest);
            for(PostEntity postEntity : postEntities)
                postDtoList.add(modelMapper.map(postEntity, PostDto.class));
        }else if(number == 4){
            UserDetailEntity userDetailEntity = getUserDetailEntityByPostDto(postUid);
            Iterable<PostEntity> postEntities = postRepository.findByDisabledAndLocationENGOrderByPostTimeDesc(false, userDetailEntity.getSubLocationENG(), pageRequest);
            for(PostEntity postEntity : postEntities)
                postDtoList.add(modelMapper.map(postEntity, PostDto.class));
        }

        for(PostDto postDto : postDtoList){
            UserEntity postUserEntity = getUserEntityByPostDto(postDto.getPostUid());
            postDto.setNickname(postUserEntity.getNickname());
            postDto.setProfileImageIcon(postUserEntity.getProfileImageIcon());
        }
        return postDtoList;
    }
    public UserEntity getUserEntityByPostDto(String postUid){
        Optional<UserEntity> optional = userRepository.findByUserUid(postUid);
        if(optional.isPresent()){
            UserEntity userEntity = optional.get();
            return userEntity;
        }
        return null;
    }
    public UserDetailEntity getUserDetailEntityByPostDto(String postUid){
        Optional<UserDetailEntity> optional = userDetailRepository.findByUserUid(postUid);
        if(optional.isPresent()){
            UserDetailEntity userDetailEntity = optional.get();
            return userDetailEntity;
        }
        return null;
    }
    public UserGradeEntity getUserGradeEntityByGradeUid(String gradeUid){
        Optional<UserGradeEntity> optional = userGradeRepository.findByGradeUid(gradeUid);
        if(optional.isPresent()){
            UserGradeEntity userGradeEntity = optional.get();
            return userGradeEntity;
        }
        return null;
    }
}
