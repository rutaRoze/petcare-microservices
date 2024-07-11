package com.roze.auth_service.controller;

import com.roze.auth_service.dto.request.AuthenticationRequest;
import com.roze.auth_service.dto.request.UserRequest;
import com.roze.auth_service.dto.response.AuthenticationResponse;
import com.roze.auth_service.service.AuthenticationService;
import com.roze.auth_service.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody UserRequest registerRequest
    ) {
        AuthenticationResponse authenticationResponse = authenticationService.register(registerRequest);

        return new ResponseEntity<>(authenticationResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest authenticateRequest
    ) throws Exception {

        return ResponseEntity.ok(authenticationService.authenticate(authenticateRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {

        tokenService.invalidateToken(token);

        return ResponseEntity.ok("Successfully logged out");
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(tokenService.validateToken(token));
    }
}
