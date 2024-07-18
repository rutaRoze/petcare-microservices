package com.roze.auth_service.persistance;

import com.roze.auth_service.persistance.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    @Query("""
            select t from TokenEntity t inner join t.authUser u
            where u.id = :authUserId and (t.isExpired = false)
            """)
    List<TokenEntity> findAllValidTokensByUser(Long authUserId);

    Optional<TokenEntity> findByToken(String token);
}
