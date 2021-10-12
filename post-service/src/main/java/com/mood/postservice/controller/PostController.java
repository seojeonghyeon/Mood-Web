package com.mood.postservice.controller;

import com.mood.postservice.auth.AuthorizationExtractor;
import com.mood.postservice.auth.BearerAuthConverser;
import com.mood.postservice.dto.HashtagDto;
import com.mood.postservice.dto.PostDto;
import com.mood.postservice.service.PostService;
import com.mood.postservice.vo.RequestHashtag;
import com.mood.postservice.vo.RequestPost;
import com.mood.postservice.vo.ResponsePost;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/")
public class PostController {
    private Environment env;
    private PostService postService;

    @Autowired
    public PostController(Environment env, PostService postService){
        this.env=env;
        this.postService=postService;
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

    @PostMapping("/registPost")
    public ResponseEntity<ResponsePost> registPost(HttpServletRequest request, @RequestBody RequestPost requestPost){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String postUid = bearerAuthConverser.handle(request, env);
        if(postUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponsePost());
        PostDto postDto = mapper.map(requestPost, PostDto.class);
        List<RequestHashtag> requestHashtagList = requestPost.getRequestHashtags();
        List<HashtagDto> hashtagDtos = new ArrayList<>();
        postDto.setPostUid(postUid);
        postDto.setHashtagDtos(hashtagDtos);
        for(RequestHashtag requestHashtag : requestHashtagList)
            hashtagDtos.add(mapper.map(requestHashtag, HashtagDto.class));
        if(postService.registPost(postDto))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponsePost());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePost());
    }

    @PostMapping("/getPosts/{page}/{}")
    public ResponseEntity<List<ResponsePost>> getPostByType(HttpServletRequest request, @PathVariable int page, @RequestBody String postType) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String postUid = bearerAuthConverser.handle(request, env);
        List<ResponsePost> responsePostList = new ArrayList<>();
        if (postUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responsePostList);
        if(postService.checkUserUid(postUid)){
            List<PostDto> postDtoList = postService.getPostByType(postUid, postType, page);
            for(PostDto postDto : postDtoList)
                responsePostList.add(mapper.map(postDto, ResponsePost.class));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responsePostList);
    }


}
