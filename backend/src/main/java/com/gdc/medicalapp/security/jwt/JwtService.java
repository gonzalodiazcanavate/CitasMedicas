package com.gdc.medicalapp.security.jwt;

import com.gdc.medicalapp.security.user.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final Key key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        String secret = properties.getSecret();
        // Validamos que jwt.secret exista y tenga al menos 32 bytes
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "Propiedad `jwt.secret` no encontrada. Añade `jwt.secret` en `src/main/resources/application.properties` o `application.yml`."
            );
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "La propiedad `jwt.secret` debe tener al menos 32 bytes (por ejemplo 32 caracteres UTF-8) para HS256."
            );
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /* Generación de Tokens */

    public String generateAccessToken(UserPrincipal user) {
        return generateToken(user, properties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(UserPrincipal user) {
        return generateToken(user, properties.getRefreshTokenExpiration());
    }

    private String generateToken(UserPrincipal user, long expiration) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(Map.of(
                        "username", user.getUsername(),
                        "role", user.getRole().name()
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* Validación de tokens */

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* Extracción de tokens */

    public Long extractUserId(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}