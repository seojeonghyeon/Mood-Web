package com.mood.postservice.service;

import com.mood.postservice.dto.HashtagDto;
import com.mood.postservice.dto.PostDto;
import com.mood.postservice.jpa.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
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
    public boolean registPost(PostDto postDto, int HOCK) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<HashtagEntity> hashtagEntityList = new ArrayList<>();

        postDto.setPostId(UUID.randomUUID().toString());
        postDto.setPostTime(LocalDateTime.now());
        postDto.setPostCommentCount(0);
        postDto.setPostLikeCount(0);
        if(HOCK==1) {
            for (HashtagDto hashtagDto : postDto.getHashtagDtos()) {
                hashtagDto.setHashtagId(UUID.randomUUID().toString());
                hashtagDto.setPostId(postDto.getPostId());
                hashtagDto.setPostUid(postDto.getPostUid());
                hashtagDto.setHashingTime(LocalDateTime.now());

                hashtagEntityList.add(modelMapper.map(hashtagDto, HashtagEntity.class));
            }
        }

        PostEntity postEntity = modelMapper.map(postDto, PostEntity.class);
        postEntity.setDisabled(false);
        postRepository.save(postEntity);
        if(HOCK==1) {
            for (HashtagEntity hashtagEntity : hashtagEntityList) {
                hashtagEntity.setDisabled(false);
                hashtagRepository.save(hashtagEntity);
            }
        }
        return true;
    }

    @Override
    public boolean updatePost(PostDto postDto, int HOCK) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Optional<PostEntity> optionalPostEntity = postRepository.findByPostIdAndPostUidAndDisabled(postDto.getPostId(), postDto.getPostUid(), false);
        optionalPostEntity.ifPresent(selectUser->{
            selectUser.setPostTime(LocalDateTime.now());
            selectUser.setLocationENG(postDto.getLocationENG());
            selectUser.setLocationKOR(postDto.getLocationKOR());
            selectUser.setPostContents(postDto.getPostContents());
            selectUser.setPostImage(postDto.getPostImage());
            postRepository.save(selectUser);
        });

        List<HashtagEntity> hashtagEntityList = new ArrayList<>();
        if(HOCK==1) {
            for (HashtagDto hashtagDto : postDto.getHashtagDtos()) {
                hashtagDto.setHashtagId(UUID.randomUUID().toString());
                hashtagDto.setPostId(postDto.getPostId());
                hashtagDto.setPostUid(postDto.getPostUid());
                hashtagDto.setHashingTime(LocalDateTime.now());

                hashtagEntityList.add(modelMapper.map(hashtagDto, HashtagEntity.class));
            }
        }

        Optional<Iterable<HashtagEntity>> optionalHashtagEntities = hashtagRepository.findByPostIdAndPostUid(postDto.getPostId(), postDto.getPostUid());
        if((HOCK==0) && (optionalHashtagEntities.isPresent())){
            for(HashtagEntity hashtagEntity : optionalHashtagEntities.get())
                switchingHashtag(hashtagEntity, true);
        }else if((HOCK==1) && (!optionalHashtagEntities.isPresent())){
            for (HashtagEntity hashtagEntity : hashtagEntityList) {
                hashtagEntity.setDisabled(false);
                hashtagRepository.save(hashtagEntity);
            }
        }else if((HOCK==1) && (optionalHashtagEntities.isPresent())){
            for(HashtagEntity hashtagEntity : hashtagEntityList){
                boolean check = true;
                for(HashtagEntity getHashtagEntity : optionalHashtagEntities.get()){
                    if(hashtagEntity.getHashtagName().equals(getHashtagEntity.getHashtagName())){
                        check = false;
                    }
                }
                if(check){
                    hashtagEntity.setDisabled(false);
                    hashtagRepository.save(hashtagEntity);
                }
            }

            for(HashtagEntity getHashtagEntity : optionalHashtagEntities.get()){
                boolean check = true;
                for(HashtagEntity hashtagEntity : hashtagEntityList){
                    if(hashtagEntity.getHashtagName().equals(getHashtagEntity.getHashtagName())){
                        check = false;
                    }
                }
                if(check){
                    switchingHashtag(getHashtagEntity, true);
                }
            }
        }
        return true;
    }

    @Override
    public boolean deletePost(String postUid, String postId) {
        Optional<PostEntity> optional = postRepository.findByPostIdAndPostUidAndDisabled(postId, postUid, false);
        optional.ifPresent(selectUser->{
            selectUser.setDisabled(true);
            postRepository.save(selectUser);
        });
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
        int number = switch (postType) {
            case "total"   -> 1;
            case "popular" -> 2;
            case "areaA"   -> 3;
            case "areaB"   -> 4;
            default -> 1;
        };
        if((!userGradeEntity.getGradeType().equals(VIP)) && (number==4))
            number = 3;


        if(number == 1){
            List<PostEntity> postEntities = postRepository.findByDisabledOrderByPostTimeDesc(false, pageRequest);
            for(PostEntity postEntity : postEntities)
                postDtoList.add(modelMapper.map(postEntity, PostDto.class));
        }else if(number == 2){
            Iterable<PostEntity> postEntities = postRepository.findByDisabledOrderByPostLikeCountDesc(false, pageRequest);
            for(PostEntity postEntity : postEntities)
                postDtoList.add(modelMapper.map(postEntity, PostDto.class));
        }else if(number == 3){
            UserDetailEntity userDetailEntity = getUserDetailEntityByPostDto(postUid);
            List<PostEntity> postEntities = postRepository.findByDisabledAndLocationENGOrderByPostTimeDesc(false, userDetailEntity.getLocationENG(), pageRequest);
            for(PostEntity postEntity : postEntities)
                postDtoList.add(modelMapper.map(postEntity, PostDto.class));
        }else if(number == 4){
            UserDetailEntity userDetailEntity = getUserDetailEntityByPostDto(postUid);
            List<PostEntity> postEntities = postRepository.findByDisabledAndLocationENGOrderByPostTimeDesc(false, userDetailEntity.getSubLocationENG(), pageRequest);
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

    @Override
    public List<PostDto> getPostsByPostUid(String postUid, int page) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<PostDto> postDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE);

        List<PostEntity> postEntities = postRepository.findByPostUidAndDisabledOrderByPostTimeDesc(postUid, false, pageRequest);
        for(PostEntity postEntity : postEntities)
            postDtoList.add(modelMapper.map(postEntity, PostDto.class));

        for(PostDto postDto : postDtoList){
            UserEntity postUserEntity = getUserEntityByPostDto(postDto.getPostUid());
            postDto.setNickname(postUserEntity.getNickname());
            postDto.setProfileImageIcon(postUserEntity.getProfileImageIcon());
        }
        return postDtoList;
    }

    @Override
    public void updateCommentCount(String postId, int number) {
        log.info("Before Update Comment Count : "+ postId +" "+" number "+ number);
        Optional<PostEntity> optional = postRepository.findByPostIdAndDisabled(postId, false);
        if(optional.isPresent()) {
            log.info("updating data : " + optional.get().getPostCommentCount());
            optional.ifPresent(selectUser->{
                selectUser.setPostCommentCount(((number)+(optional.get().getPostCommentCount())));
                postRepository.save(selectUser);
            });
        }
        log.info("After update");
    }

    @Override
    public void updateLikeCount(String postId, int number) {
        Optional<PostEntity> optional = postRepository.findByPostIdAndDisabled(postId, false);
        if(optional.isPresent()) {
            optional.ifPresent(selectUser -> {
                selectUser.setPostLikeCount(((number)+(optional.get().getPostLikeCount())));
                postRepository.save(selectUser);
            });
        }
    }

    @Override
    public List<PostDto> getPostByHashtag(String hashtagName, int page) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<PostDto> postDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        List<HashtagEntity> hashtagEntityList = hashtagRepository.findByHashtagNameAndDisabledOrderByHashingTimeDesc(hashtagName, false, pageRequest);
        for(HashtagEntity hashtagEntity : hashtagEntityList){
            Optional<PostEntity> optionalPostEntity = postRepository.findByPostIdAndDisabled(hashtagEntity.getPostId(), false);
            if(optionalPostEntity.isPresent())
                postDtoList.add(modelMapper.map(optionalPostEntity.get(), PostDto.class));
        }

        return postDtoList;
    }

    @Override
    public PostDto getPostByPostId(String postId) {
        PostDto postDto = null;
        Optional<PostEntity> optionalPostEntity = postRepository.findByPostIdAndDisabled(postId, false);
        if(optionalPostEntity.isPresent()){
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            postDto = modelMapper.map(optionalPostEntity.get(), PostDto.class);
            UserEntity postUserEntity = getUserEntityByPostDto(postDto.getPostUid());
            postDto.setNickname(postUserEntity.getNickname());
            postDto.setProfileImageIcon(postUserEntity.getProfileImageIcon());
        }
        return postDto;
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
    public void switchingHashtag(HashtagEntity hashtagEntity, boolean disabled){
        Optional<HashtagEntity> optional = hashtagRepository.findByPostUidAndDisabledAndHashtagName(hashtagEntity.getPostUid(), !disabled, hashtagEntity.getHashtagName());
        optional.ifPresent(selectUser->{
            selectUser.setDisabled(disabled);
            hashtagRepository.save(selectUser);
        });
    }
}
