package com.mood.lockservice.client;

import com.mood.lockservice.vo.ResponseLockUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://127.0.0.1:8000")
public interface UserServiceClient {
    @GetMapping("/user-service/userUid/updateLockUser/{userUid}")
    ResponseLockUser updateLockUser(@PathVariable String userUid);

    @GetMapping("/user-service/userUid/exist/{userUid}")
    ResponseLockUser checkUserUid(@PathVariable String userUid);
}
