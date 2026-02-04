package com.gdc.medicalapp.repositories;

import com.gdc.medicalapp.domain.entities.PasswordResetToken;
import com.gdc.medicalapp.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /**
     * Invalida todos los tokens anteriores del usuario
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user = :user AND t.used = false")
    void invalidateAllTokensForUser(@Param("user") User user);

    /**
     * Elimina tokens expirados (para limpieza periódica)
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Cuenta tokens activos (no usados y no expirados) para un usuario
     */
    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.user = :user AND t.used = false AND t.expiresAt > :now")
    long countActiveTokensForUser(@Param("user") User user, @Param("now") Instant now);
}
