package com.gdc.medicalapp.security.jwt;

import com.gdc.medicalapp.domain.entities.RefreshToken;
import com.gdc.medicalapp.domain.entities.User;
import com.gdc.medicalapp.repositories.RefreshTokenRepository;
import com.gdc.medicalapp.security.crypto.TokenHasher;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            JwtProperties jwtProperties
    ) {
        this.repository = repository;
        this.jwtProperties = jwtProperties;
    }

    public RefreshToken create(User user, String rawToken) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(
                TokenHasher.hash(rawToken)
        );
        refreshToken.setExpiresAt(
                Instant.now().plusMillis(
                        jwtProperties.getRefreshTokenExpiration()
                )
        );

        return repository.save(refreshToken);
    }


    public RefreshToken verify(String rawToken) {

        String hash = TokenHasher.hash(rawToken);

        RefreshToken refreshToken = repository
                .findByTokenHash(hash)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token no existe")
                );

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revocado");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expirado");
        }

        return refreshToken;
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }
}
