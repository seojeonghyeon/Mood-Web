package com.mood.postservice.service;

import com.mood.postservice.dto.CommentDto;
import com.mood.postservice.jpa.CommentEntity;
import com.mood.postservice.jpa.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository){
        this.commentRepository=commentRepository;
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
}
