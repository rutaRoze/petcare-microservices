package com.roze.auth_service.service;

import com.roze.auth_service.dto.SecurityUserDetails;
import com.roze.auth_service.exception.AuthenticationFailedException;
import com.roze.auth_service.mapper.SecurityUserDetailsMapper;
import com.roze.auth_service.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityUserDetailsMapper securityUserDetailsMapper;


    public SecurityUserDetails findUserByEmail(String email) {
        return securityUserDetailsMapper
                .mapToSecurityUserDetails(userRepository.findByEmail(email)
                        .orElseThrow(() -> new AuthenticationFailedException("Invalid email or password")));
    }
}
