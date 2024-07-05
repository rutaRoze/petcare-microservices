package com.roze.auth_service.feign;

import com.roze.auth_service.dto.request.UserProfileRequest;
import com.roze.auth_service.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {
    @PostMapping("/api/v1/users")
    UserProfileResponse createUser(@RequestBody UserProfileRequest userProfileRequest);
}