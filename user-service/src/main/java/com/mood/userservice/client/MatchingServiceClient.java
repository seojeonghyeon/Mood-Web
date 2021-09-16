package com.mood.userservice.client;

import com.mood.userservice.vo.RequestMatchingUser;
import com.mood.userservice.vo.ResponseMatchingUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "matching-service")
public interface MatchingServiceClient {
    @PostMapping("/matching-service/updateMatchingUsers")
    List<ResponseMatchingUser> updateMatchingUsers(@RequestBody List<RequestMatchingUser> requestMatchingUsers);
}
