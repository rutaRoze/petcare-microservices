package com.roze.auth_service.kafka.consumer;

import com.roze.auth_service.service.AuthenticationService;
import com.roze.auth_service.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserServiceEventConsumer {

    private final AuthenticationService authenticationService;
    private final UserProfileService userProfileService;

    public UserServiceEventConsumer(AuthenticationService authenticationService, UserProfileService userProfileService) {
        this.authenticationService = authenticationService;
        this.userProfileService = userProfileService;
    }

    @KafkaListener(topics = "user-deletion-events", groupId = "auth-service-group")
    public void handleUserDeletionEvent(Long userProfileId) {
        log.info("Consumer received user deletion event from Kafka server: userProfileId={}", userProfileId);
        authenticationService.deleteUserByProfileId(userProfileId);
    }

    @KafkaListener(topics = "cache-eviction-events", groupId = "auth-service-group")
    public void handleCacheEvictionEvent(Long userProfileId) {
        log.info("Consumer received cache eviction event from Kafka server: userProfileId={}", userProfileId);
        userProfileService.evictCache(userProfileId);
    }
}
