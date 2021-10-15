package com.mood.postservice.controller;

import com.mood.postservice.auth.AuthorizationExtractor;
import com.mood.postservice.auth.BearerAuthConverser;
import com.mood.postservice.dto.CommentDto;
import com.mood.postservice.dto.HashtagDto;
import com.mood.postservice.dto.LikeDto;
import com.mood.postservice.dto.PostDto;
import com.mood.postservice.service.CommentService;
import com.mood.postservice.service.LikeService;
import com.mood.postservice.service.PostService;
import com.mood.postservice.vo.*;
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
    private CommentService commentService;
    private LikeService likeService;

    @Autowired
    public PostController(Environment env, PostService postService, CommentService commentService, LikeService likeService){
        this.env=env;
        this.postService=postService;
        this.commentService=commentService;
        this.likeService=likeService;
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

    //##
    @PostMapping("/registPost")
    public ResponseEntity<ResponsePost> registPost(HttpServletRequest request, @RequestBody RequestPost requestPost){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String postUid = bearerAuthConverser.handle(request, env);
        if(postUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponsePost());
        PostDto postDto = mapper.map(requestPost, PostDto.class);
        List<RequestHashtag> requestHashtagList;
        int HOCK = 0;
        if(!requestPost.getRequestHashtags().equals(null))
            HOCK=1;

        List<HashtagDto> hashtagDtos = new ArrayList<>();
        postDto.setPostUid(postUid);
        if(HOCK==1) {
            requestHashtagList = requestPost.getRequestHashtags();
            for (RequestHashtag requestHashtag : requestHashtagList)
                hashtagDtos.add(mapper.map(requestHashtag, HashtagDto.class));
        }
        postDto.setHashtagDtos(hashtagDtos);

        if(postService.registPost(postDto, HOCK))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponsePost());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePost());
    }

    //##
    @PostMapping("/updatePost")
    public ResponseEntity<ResponsePost> updatePost(HttpServletRequest request, @RequestBody RequestPost requestPost){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String postUid = bearerAuthConverser.handle(request, env);
        if(postUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponsePost());
        PostDto postDto = mapper.map(requestPost, PostDto.class);
        List<RequestHashtag> requestHashtagList;
        int HOCK = 0;
        if(!requestPost.getRequestHashtags().equals(null))
            HOCK=1;

        List<HashtagDto> hashtagDtos = new ArrayList<>();
        postDto.setPostUid(postUid);
        if(HOCK==1) {
            requestHashtagList = requestPost.getRequestHashtags();
            for (RequestHashtag requestHashtag : requestHashtagList)
                hashtagDtos.add(mapper.map(requestHashtag, HashtagDto.class));
        }
        postDto.setHashtagDtos(hashtagDtos);

        if(postService.updatePost(postDto, HOCK))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponsePost());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePost());
    }

    //##
    @PostMapping("/deletePost")
    public ResponseEntity<ResponsePost> deletePost(HttpServletRequest request, @RequestBody RequestPost requestPost){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String postUid = bearerAuthConverser.handle(request, env);
        if(postUid.equals(null) && requestPost.getPostId().equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponsePost());
        if(postService.deletePost(postUid, requestPost.getPostId()))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponsePost());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePost());
    }

    //##
    @PostMapping("/getPosts/{page}/{postType}")
    public ResponseEntity<List<ResponsePost>> getPostsByType(HttpServletRequest request, @PathVariable int page, @PathVariable String postType) {
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

    //##
    @PostMapping("/getPosts/{page}")
    public ResponseEntity<List<ResponsePost>> getPostsByPostUid(HttpServletRequest request, @PathVariable int page, @RequestBody RequestPost requestPost) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        List<ResponsePost> responsePostList = new ArrayList<>();
        if (userUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responsePostList);
        if (requestPost.getPostUid().equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responsePostList);
        if(postService.checkUserUid(userUid)){
            List<PostDto> postDtoList = postService.getPostsByPostUid(requestPost.getPostUid(), page);
            for(PostDto postDto : postDtoList)
                responsePostList.add(mapper.map(postDto, ResponsePost.class));
            return ResponseEntity.status(HttpStatus.OK).body(responsePostList);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responsePostList);
    }

    //##
    @PostMapping("/getHashtag/{hashtagName}/{page}")
    public ResponseEntity<List<ResponsePost>> getHashtag(HttpServletRequest request, @PathVariable String hashtagName, @PathVariable int page) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String userUid = bearerAuthConverser.handle(request, env);
        List<ResponsePost> responsePostList = new ArrayList<>();
        if (userUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responsePostList);
        if (hashtagName.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responsePostList);
        if(postService.checkUserUid(userUid)){
            List<PostDto> postDtoList = postService.getPostByHashtag(hashtagName, page);
            for(PostDto postDto : postDtoList)
                responsePostList.add(mapper.map(postDto, ResponsePost.class));
            return ResponseEntity.status(HttpStatus.OK).body(responsePostList);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responsePostList);
    }

    //##
    @GetMapping("/getPost/{postId}")
    public ResponseEntity<ResponsePost> getHashtag(@PathVariable String postId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<ResponseComment> responseComments = new ArrayList<>();
        if (postId.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponsePost());
        PostDto postDto = postService.getPostByPostId(postId);
        List<CommentDto> commentDtoList = commentService.getCommentByPostId(postId);
        for(CommentDto commentDto: commentDtoList) {
            ResponseComment responseComment = mapper.map(commentDto, ResponseComment.class);
            if(commentDto.isDisabled()==true)
                responseComment.setCommentContents("삭제된 내용입니다.");
            responseComments.add(responseComment);
        }
        ResponsePost responsePost = mapper.map(postDto, ResponsePost.class);
        responsePost.setResponseCommentInfoList(responseComments);
        return ResponseEntity.status(HttpStatus.OK).body(responsePost);
    }

    //##
    @PostMapping("/registComment")
    public ResponseEntity<ResponseComment> registComment(HttpServletRequest request, @RequestBody RequestComment requestComment) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String commentUid = bearerAuthConverser.handle(request, env);
        if (commentUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseComment());
        CommentDto commentDto = mapper.map(requestComment, CommentDto.class);
        commentDto.setCommentUid(commentUid);
        if(commentService.registComment(commentDto)) {
            postService.updateCommentCount(commentDto.getPostId(), 1);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseComment());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseComment());
    }

    //##
    @PostMapping("/deleteComment")
    public ResponseEntity<ResponseComment> deleteComment(HttpServletRequest request, @RequestBody RequestComment requestComment) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String commentUid = bearerAuthConverser.handle(request, env);
        if (commentUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseComment());
        CommentDto commentDto = mapper.map(requestComment, CommentDto.class);
        commentDto.setCommentUid(commentUid);
        if(commentService.deleteComment(commentDto)) {
            postService.updateCommentCount(commentDto.getPostId(), -1);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseComment());
    }

    //##
    @PostMapping("/clickLike/{postId}/{commentId}")
    public ResponseEntity<ResponsePost> clickLike(HttpServletRequest request, @PathVariable String postId, @PathVariable String commentId) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BearerAuthConverser bearerAuthConverser = new BearerAuthConverser(new AuthorizationExtractor());
        String likeUid = bearerAuthConverser.handle(request, env);
        if (likeUid.equals(null))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponsePost());
        if(postService.checkUserUid(likeUid)){
            LikeDto likeDto = new LikeDto();
            likeDto.setLikeUid(likeUid);
            likeDto.setPostId(postId);
            likeDto.setCommentId(commentId);
            boolean check = likeService.updateLike(likeDto);
            int number = 1;
            if(check)
                number = -1;
            if(commentId.equals("0")){
                postService.updateLikeCount(postId, number);
            }else{
                commentService.updateLikeCount(postId, commentId, number);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponsePost());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePost());
    }

    @PostMapping("/getList")
    public ResponseEntity<ResponsePost> clickLike(HttpServletRequest request) {
        ResponsePost responsePost = new ResponsePost();
        List<ResponseComment> responseCommentInfoList = new ArrayList<>();
        responsePost.setPostId("abc");
        responsePost.setPostUid("34ad0255-33af-4685-b920-a7112540d505");
        responsePost.setPostImage("https://firebasestorage.googleapis.com/v0/b/mood-d39e8.appspot.com/o/profileImg%2FJPEG__20211010_152520_.png?alt=media&token=b30ff108-46bb-4f83-bc8c-2203df77505c");
        responsePost.setPostContents("인생의 품었기 얼마나 위하여 대고, 가는 끓는 예수는 끓는다. 가장 청춘 끓는 같으며, 사라지지 천고에 가슴이 쓸쓸하랴? 청춘 인류의 가지에 장식하는 뜨고, 피에 능히 품으며, 그리하였는가? #hi #hello #안녕 #서울");
        for(int i=0; i < 4; i++){
            ResponseComment responseCommentInfo = new ResponseComment();
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
