package com.roze.auth_service.service;

import com.roze.auth_service.dto.SecurityUserDetails;
import com.roze.auth_service.dto.response.UserProfileResponse;
import com.roze.auth_service.exception.AuthenticationFailedException;
import com.roze.auth_service.feign.UserServiceClient;
import com.roze.auth_service.mapper.SecurityUserDetailsMapper;
import com.roze.auth_service.persistance.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityUserService {

    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private SecurityUserDetailsMapper securityUserDetailsMapper;
    @Autowired
    private UserServiceClient userServiceClient;


    public SecurityUserDetails findUserByEmail(String email) {
        SecurityUserDetails securityUserDetails = securityUserDetailsMapper
                .mapToSecurityUserDetails(authUserRepository.findByEmail(email)
                        .orElseThrow(() -> new AuthenticationFailedException("Invalid email or password")));

        UserProfileResponse userProfile = userServiceClient.getUserById(securityUserDetails.getUserProfileId());
        log.debug("UserProfileResponse fetched: {}", userProfile);

        securityUserDetails.setRoleList(userProfile.getRoleNames());
        log.debug("Roles set in securityUserDetails: {}", securityUserDetails.getRoleList());

        return securityUserDetails;
    }
}
