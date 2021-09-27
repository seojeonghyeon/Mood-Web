package com.mood.lockservice.client;

import com.mood.lockservice.vo.RequestLockUser;
import com.mood.lockservice.vo.RequestUser;
import com.mood.lockservice.vo.ResponseLockUser;
import com.mood.lockservice.vo.ResponseUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping("/user-service/updateLockUser")
    ResponseLockUser updateLockUser(@RequestBody RequestLockUser requestLockUser);

    @PostMapping("/user-service/userUid/exist")
    ResponseLockUser checkUserUid(@RequestBody RequestUser requestUser);
}
