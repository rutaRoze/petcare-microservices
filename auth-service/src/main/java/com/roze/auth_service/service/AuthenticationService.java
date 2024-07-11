package com.roze.auth_service.service;

import com.roze.auth_service.dto.request.AuthenticationRequest;
import com.roze.auth_service.dto.request.UserProfileRequest;
import com.roze.auth_service.dto.request.UserRequest;
import com.roze.auth_service.dto.response.AuthenticationResponse;
import com.roze.auth_service.dto.response.UserProfileResponse;
import com.roze.auth_service.enums.TokenType;
import com.roze.auth_service.exception.AuthenticationFailedException;
import com.roze.auth_service.exception.UserAlreadyExist;
import com.roze.auth_service.feign.UserServiceClient;
import com.roze.auth_service.mapper.AuthUserMapper;
import com.roze.auth_service.mapper.SecurityUserDetailsMapper;
import com.roze.auth_service.mapper.UserProfileMapper;
import com.roze.auth_service.persistance.AuthUserRepository;
import com.roze.auth_service.persistance.TokenRepository;
import com.roze.auth_service.persistance.model.AuthUserEntity;
import com.roze.auth_service.persistance.model.TokenEntity;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private AuthUserMapper authUserMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityUserDetailsMapper securityUserDetailsMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private TokenRepository tokenRepository;

    public AuthenticationResponse register(UserRequest registerRequest) {

        if (checkIfUserExistsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExist(String.format("User with email %s already exists", registerRequest.getEmail()));
        }

        UserProfileRequest userProfileRequest = userProfileMapper.userRequestToUserProfileRequest(registerRequest);
        UserProfileResponse userProfileResponse = createUserProfileByIdOrThrow(userProfileRequest);

        AuthUserEntity userToSave = authUserMapper.userRequestToAuthUserEntity(registerRequest);

        log.debug("User request: {}", userToSave);


        userToSave.setEmail(sanitizeData(registerRequest.getEmail()).toLowerCase(Locale.ROOT));
        userToSave.setPassword(passwordEncoder.encode(sanitizeData(registerRequest.getPassword())));
        userToSave.setUserProfileId(userProfileResponse.getId());

        AuthUserEntity savedUser = authUserRepository.save(userToSave);

        log.debug("User saved: {}", savedUser);

        String jwtToken = jwtService.generateToken(securityUserDetailsMapper.mapToSecurityUserDetails(savedUser));

        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userEmail(savedUser.getEmail())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticateRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticateRequest.getEmail(),
                            authenticateRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid email or password");
        }

        AuthUserEntity authUser = authUserRepository.findByEmail(authenticateRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        log.debug("User to authenticate: {}", authUser);

        String jwtToken = jwtService.generateToken(securityUserDetailsMapper.mapToSecurityUserDetails(authUser));

        revokeAllUserTokens(authUser);
        saveUserToken(authUser, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userEmail(authUser.getEmail())
                .build();
    }

    private boolean checkIfUserExistsByEmail(String email) {
        return authUserRepository.existsByEmailIgnoreCase(email);
    }

    private String sanitizeData(String data) {
        if (data == null) {
            return null;
        }

        return data.trim();
    }

    private UserProfileResponse createUserProfileByIdOrThrow(UserProfileRequest userProfileRequest) {
        try {
            return userServiceClient.createUser(userProfileRequest);
        } catch (FeignException.Conflict e) {
            throw new UserAlreadyExist(String.format("User with email %s already exists", userProfileRequest.getEmail()));
        }
    }

    public void revokeAllUserTokens(AuthUserEntity authUser) {
        List<TokenEntity> validUserTokens = tokenRepository.findAllValidTokensByUser(authUser.getId());
        if(validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    public void saveUserToken(AuthUserEntity authUser, String jwtToken) {
        TokenEntity token = TokenEntity.builder()
                .authUser(authUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .isExpired(false)
                .build();

        tokenRepository.save(token);
    }
}
