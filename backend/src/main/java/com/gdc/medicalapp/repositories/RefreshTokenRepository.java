package com.gdc.medicalapp.repositories;

import com.gdc.medicalapp.domain.entities.RefreshToken;
import com.gdc.medicalapp.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserAndRevokedFalseOrderByExpiresAtDesc(User user);

    List<RefreshToken> findByUserAndRevokedFalse(User user);

    void deleteByUserId(Long userId);

    void deleteByUserAndExpiresAtBefore(User user, Instant expiresAt);

    void deleteByExpiresAtBefore(Instant expiresAt);
}
