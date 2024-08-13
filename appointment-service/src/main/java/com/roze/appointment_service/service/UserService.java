package com.roze.appointment_service.service;

import com.roze.appointment_service.dto.response.UserResponse;
import com.roze.appointment_service.exception.NotFoundException;
import com.roze.appointment_service.exception.ServiceUnavailableException;
import com.roze.appointment_service.feign.UserClient;
import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserClient userClient;

    @Retry(name = "userServiceRetry", fallbackMethod = "fallbackToRetrieveUserData")
    public UserResponse getUserByIdOrThrow(Long userId) {
        log.info("Attempting to retrieve user with ID: {}", userId);
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            log.info("User not found on attempt: {}", userId);
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

    private UserResponse fallbackToRetrieveUserData(Long userId, Throwable throwable) {

        log.error("Fallback method invoked for userId: {}. Error: {}", userId, throwable.getMessage());
        throw new ServiceUnavailableException("Service is currently unavailable. Please try again later.");
    }
}
