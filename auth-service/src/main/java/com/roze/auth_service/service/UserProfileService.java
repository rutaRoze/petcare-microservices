package com.roze.auth_service.service;

import com.roze.auth_service.dto.response.UserProfileResponse;
import com.roze.auth_service.enums.RoleName;
import com.roze.auth_service.feign.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserProfileService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Cacheable(value = "userRoleCache", key = "#userProfileId", unless = "#result == null")
    public List<RoleName> getUserRoles(Long userProfileId) {
        UserProfileResponse userProfile = userServiceClient.getUserById(userProfileId);
        log.debug("UserProfileResponse fetched: {}", userProfile);
        return userProfile.getRoleNames();
    }

    @CacheEvict(value = "userRoleCache", key = "#userProfileId")
    public void evictCache(Long userProfileId) {
        log.debug("Cache evicted for userProfileId: {}", userProfileId);
    }
}
