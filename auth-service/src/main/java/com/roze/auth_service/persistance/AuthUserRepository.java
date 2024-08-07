package com.roze.auth_service.persistance;

import com.roze.auth_service.persistance.model.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUserEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);
    Optional<AuthUserEntity> findByEmail(String email);
    Optional<AuthUserEntity> findByUserProfileId(Long userProfileId);
}
