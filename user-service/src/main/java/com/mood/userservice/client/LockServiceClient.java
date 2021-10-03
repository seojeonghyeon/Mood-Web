package com.mood.userservice.client;

import com.mood.userservice.vo.RequestLockUser;
import com.mood.userservice.vo.ResponseLockUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "lock-service")
public interface LockServiceClient {
    @PostMapping("/lock-service/getLockUser")
    List<ResponseLockUser> getLockUser(@RequestBody RequestLockUser requestLockUser);
}
