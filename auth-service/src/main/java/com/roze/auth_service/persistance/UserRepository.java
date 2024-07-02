package com.roze.auth_service.persistance;

import com.roze.auth_service.persistance.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);
    Optional<UserEntity> findByEmail(String email);
}
