package com.roze.auth_service.mapper;

import com.roze.auth_service.dto.SecurityUserDetails;
import com.roze.auth_service.persistance.model.AuthUserEntity;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserDetailsMapper {

    public SecurityUserDetails mapToSecurityUserDetails(AuthUserEntity authUserEntity) {
        return SecurityUserDetails.builder()
                .id(authUserEntity.getId())
                .email(authUserEntity.getEmail())
                .password(authUserEntity.getPassword())
                .userProfileId(authUserEntity.getUserProfileId())
                .build();
    }
}