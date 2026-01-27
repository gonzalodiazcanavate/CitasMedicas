package com.gdc.medicalapp.security.jwt;

import com.gdc.medicalapp.domain.entities.RefreshToken;
import com.gdc.medicalapp.domain.entities.User;
import com.gdc.medicalapp.repositories.RefreshTokenRepository;
import com.gdc.medicalapp.security.crypto.TokenHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtProperties jwtProperties;

    // Límite de sesiones activas por usuario
    private static final int MAX_ACTIVE_SESSIONS = 5;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            JwtProperties jwtProperties
    ) {
        this.repository = repository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public RefreshToken create(User user, String rawToken, long expirationMillis) {
        if (user == null || rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Usuario y token no pueden ser nulos");
        }

        // Limitar número de sesiones activas
        List<RefreshToken> userTokens = repository.findByUserAndRevokedFalseOrderByExpiresAtDesc(user);
        if (userTokens.size() >= MAX_ACTIVE_SESSIONS) {
            // Revocar el token más antiguo
            RefreshToken oldestToken = userTokens.get(userTokens.size() - 1);
            revoke(oldestToken);
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(TokenHasher.hash(rawToken));
        refreshToken.setExpiresAt(
                Instant.now().plusMillis(expirationMillis)
        );

        return repository.save(refreshToken);
    }


    public RefreshToken verify(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Token no puede ser nulo");
        }

        String hash = TokenHasher.hash(rawToken);

        RefreshToken refreshToken = repository
                .findByTokenHash(hash)
                .orElseThrow(() ->
                        new IllegalStateException("Refresh token no existe")
                );

        if (refreshToken.isRevoked()) {
            throw new IllegalStateException("Refresh token revocado");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Refresh token expirado");
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }

    @Transactional
    public void revokeByToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        String hash = TokenHasher.hash(rawToken);
        repository.findByTokenHash(hash).ifPresent(this::revoke);
    }

    @Transactional
    public void revokeAllByUser(User user) {
        List<RefreshToken> tokens = repository.findByUserAndRevokedFalse(user);
        tokens.forEach(this::revoke);
    }

    @Transactional
    public void deleteExpiredTokensByUser(User user) {
        repository.deleteByUserAndExpiresAtBefore(user, Instant.now());
    }

    @Transactional
    public void deleteAllExpiredTokens() {
        repository.deleteByExpiresAtBefore(Instant.now());
    }
}
