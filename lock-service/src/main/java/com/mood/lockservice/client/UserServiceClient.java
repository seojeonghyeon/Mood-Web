package com.mood.lockservice.client;

import com.mood.lockservice.vo.ResponseLockUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://172.18.0.8:8000")
public interface UserServiceClient {
    @GetMapping("/user-service/userUid/updateLockUser/{userUid}/{lockBoolean}")
    ResponseLockUser updateLockUser(@PathVariable String userUid,@PathVariable boolean lockBoolean);

    @GetMapping("/user-service/userUid/exist/{userUid}")
    ResponseLockUser checkUserUid(@PathVariable String userUid);
}
