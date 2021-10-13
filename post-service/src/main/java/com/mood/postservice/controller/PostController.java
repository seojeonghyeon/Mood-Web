package com.mood.postservice.controller;

import com.mood.postservice.auth.AuthorizationExtractor;
import com.mood.postservice.auth.BearerAuthConverser;
import com.mood.postservice.dto.HashtagDto;
import com.mood.postservice.dto.PostDto;
import com.mood.postservice.service.PostService;
import com.mood.postservice.vo.RequestHashtag;
import com.mood.postservice.vo.RequestPost;
import com.mood.postservice.vo.ResponseCommentInfo;
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
import java.time.LocalDateTime;
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
        log.info("Request Post : " + requestPost);
        PostDto postDto = mapper.map(requestPost, PostDto.class);
        List<RequestHashtag> requestHashtagList;
        int HOCK = 0;
        if(!requestPost.getRequestHashtags().get(0).getHashtagName().isBlank())
            HOCK=1;

        List<HashtagDto> hashtagDtos = new ArrayList<>();
        postDto.setPostUid(postUid);
        postDto.setHashtagDtos(hashtagDtos);
        if(HOCK==1) {
            requestHashtagList = requestPost.getRequestHashtags();
            for (RequestHashtag requestHashtag : requestHashtagList)
                hashtagDtos.add(mapper.map(requestHashtag, HashtagDto.class));
        }
        if(postService.registPost(postDto, HOCK))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponsePost());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePost());
    }

    @PostMapping("/getPosts/{page}/{postType}")
    public ResponseEntity<List<ResponsePost>> getPostByType(HttpServletRequest request, @PathVariable int page, @PathVariable String postType) {
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
            return ResponseEntity.status(HttpStatus.OK).body(responsePostList);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responsePostList);
    }

    @PostMapping("/clickLike/{postId}/{commentId}")
    public ResponseEntity<ResponsePost> clickLike(HttpServletRequest request, @PathVariable String postId, @RequestBody String commentId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String postUid = bearerAuthConverser.handle(request, env);
        if (postUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponsePost());
        if(postService.checkUserUid(postUid)){

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePost());
    }
    @PostMapping("/getList")
    public ResponseEntity<ResponsePost> clickLike(HttpServletRequest request) {
        ResponsePost responsePost = new ResponsePost();
        List<ResponseCommentInfo> responseCommentInfoList = new ArrayList<>();
        responsePost.setPostId("abc");
        responsePost.setPostUid("34ad0255-33af-4685-b920-a7112540d505");
        responsePost.setPostImage("https://firebasestorage.googleapis.com/v0/b/mood-d39e8.appspot.com/o/profileImg%2FJPEG__20211010_152520_.png?alt=media&token=b30ff108-46bb-4f83-bc8c-2203df77505c");
        responsePost.setPostContents("인생의 품었기 얼마나 위하여 대고, 가는 끓는 예수는 끓는다. 가장 청춘 끓는 같으며, 사라지지 천고에 가슴이 쓸쓸하랴? 청춘 인류의 가지에 장식하는 뜨고, 피에 능히 품으며, 그리하였는가? #hi #hello #안녕 #서울");
        for(int i=0; i < 4; i++){
            ResponseCommentInfo responseCommentInfo = new ResponseCommentInfo();
            responseCommentInfo.setCommentId("bcd : "+i);
            responseCommentInfo.setCommentContents("hello!");
            responseCommentInfo.setCommentGroup(1);
            responseCommentInfo.setCommentLikeCount(10);
            responseCommentInfo.setCommentTime(LocalDateTime.now());
            responseCommentInfoList.add(responseCommentInfo);
        }
        responsePost.setResponseCommentInfoList(responseCommentInfoList);
        return ResponseEntity.status(HttpStatus.OK).body(responsePost);
    }

}
