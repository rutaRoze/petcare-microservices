package com.roze.auth_service.service;

import com.roze.auth_service.dto.SecurityUserDetails;
import com.roze.auth_service.enums.RoleName;
import com.roze.auth_service.exception.AuthenticationFailedException;
import com.roze.auth_service.mapper.SecurityUserDetailsMapper;
import com.roze.auth_service.persistance.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SecurityUserService {

    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private SecurityUserDetailsMapper securityUserDetailsMapper;
    @Autowired
    private UserProfileService userProfileService;


    public SecurityUserDetails findUserByEmail(String email) {
        SecurityUserDetails securityUserDetails = securityUserDetailsMapper
                .mapToSecurityUserDetails(authUserRepository.findByEmail(email)
                        .orElseThrow(() -> new AuthenticationFailedException("Invalid email or password")));

        List<RoleName> userRoles = userProfileService.getUserRoles(securityUserDetails.getUserProfileId());
        log.debug("User Roles fetched: {}", userRoles);

        securityUserDetails.setRoleList(userRoles);
        log.debug("Roles set in securityUserDetails: {}", securityUserDetails.getRoleList());

        return securityUserDetails;
    }
}
