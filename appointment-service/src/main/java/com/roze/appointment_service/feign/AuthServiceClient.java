package com.roze.appointment_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthServiceClient {
    @GetMapping("/api/v1/auth/validate")
    Boolean validateToken(@RequestHeader("Authorization") String token);
}