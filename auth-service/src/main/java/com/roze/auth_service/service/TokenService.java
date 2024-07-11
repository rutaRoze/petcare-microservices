package com.roze.auth_service.service;


import com.roze.auth_service.persistance.TokenRepository;
import com.roze.auth_service.persistance.model.TokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public void invalidateToken(String token) {

        Optional<TokenEntity> tokenEntity = findToken(token);

        if (tokenEntity.isPresent()) {
            TokenEntity tokenToInvalidate = tokenEntity.get();
            tokenToInvalidate.setExpired(true);
            tokenRepository.save(tokenToInvalidate);
        }
    }

    public boolean validateToken(String token) {

        Optional<TokenEntity> tokenEntity = findToken(token);

        if (tokenEntity.isPresent()) {
            TokenEntity tokenToValidate = tokenEntity.get();
            return !tokenToValidate.isExpired();
        }

        return false;
    }

    private Optional<TokenEntity> findToken(String token) {
        String jwtToken = token.substring(7);
        return tokenRepository.findByToken(jwtToken);
    }
}
