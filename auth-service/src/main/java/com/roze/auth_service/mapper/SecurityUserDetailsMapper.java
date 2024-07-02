package com.roze.auth_service.mapper;

import com.roze.auth_service.dto.SecurityUserDetails;
import com.roze.auth_service.persistance.model.RoleEntity;
import com.roze.auth_service.persistance.model.UserEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SecurityUserDetailsMapper {

    public SecurityUserDetails mapToSecurityUserDetails(UserEntity userEntity) {
        return SecurityUserDetails.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roleList(userEntity.getRoles().stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}
