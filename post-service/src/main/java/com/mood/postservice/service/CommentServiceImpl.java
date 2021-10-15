package com.mood.postservice.service;

import com.mood.postservice.dto.CommentDto;
import com.mood.postservice.dto.PostDto;
import com.mood.postservice.jpa.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    UserRepository userRepository;
    UserDetailRepository userDetailRepository;
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, UserDetailRepository userDetailRepository){
        this.commentRepository=commentRepository;
        this.userRepository=userRepository;
        this.userDetailRepository=userDetailRepository;
    }

    @Override
    public boolean registComment(CommentDto commentDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        commentDto.setCommentId(UUID.randomUUID().toString());
        commentDto.setCommentTime(LocalDateTime.now());
        commentDto.setDisabled(false);
        commentDto.setCommentLikeCount(0);
        if(commentDto.getCommentGroup()==0)
            commentDto.setCommentClass(commentRepository.countByPostId(commentDto.getPostId())+1);
        CommentEntity commentEntity = mapper.map(commentDto, CommentEntity.class);
        commentRepository.save(commentEntity);
        return true;
    }

    @Override
    public boolean deleteComment(CommentDto commentDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Optional<CommentEntity> optional = commentRepository.findByPostIdAndDisabledAndCommentIdAndCommentUid(commentDto.getPostId(),false,commentDto.getCommentId(),commentDto.getCommentUid());
        optional.ifPresent(selectUser->{
            selectUser.setDisabled(true);
            selectUser.setCommentTime(LocalDateTime.now());
            commentRepository.save(selectUser);
        });
        return true;
    }

    @Override
    public void updateLikeCount(String postId, String commentId, int number) {
        Optional<CommentEntity> optional = commentRepository.findByPostIdAndCommentIdAndDisabled(postId, commentId, false);
        optional.ifPresent(selectUser->{
            selectUser.setCommentLikeCount(((number)+(optional.get().getCommentLikeCount())));
            commentRepository.save(selectUser);
        });
    }

    @Override
    public List<CommentDto> getCommentByPostId(String postId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<CommentDto> commentDtoList = new ArrayList<>();

        Optional<Iterable<CommentEntity>> optionalCommentEntities = commentRepository.findByPostIdOrderByCommentClass(postId);
        if(optionalCommentEntities.isPresent()){
            Iterable<CommentEntity> iterable = optionalCommentEntities.get();
            for(CommentEntity commentEntity : iterable) {
                commentDtoList.add(mapper.map(commentEntity, CommentDto.class));
            }
            for(CommentDto commentDto : commentDtoList){
                UserEntity postUserEntity = getUserEntityByPostDto(commentDto.getCommentUid());
                commentDto.setNickname(postUserEntity.getNickname());
                commentDto.setProfileImageIcon(postUserEntity.getProfileImageIcon());
            }
        }
        return commentDtoList;
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
}
