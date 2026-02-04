package com.gdc.medicalapp.services;

import com.gdc.medicalapp.domain.entities.PasswordResetToken;
import com.gdc.medicalapp.domain.entities.User;
import com.gdc.medicalapp.repositories.PasswordResetTokenRepository;
import com.gdc.medicalapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.token-expiration-minutes:30}")
    private int tokenExpirationMinutes;

    @Value("${app.password-reset.max-requests-per-hour:3}")
    private int maxRequestsPerHour;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     * Genera un token, lo almacena y envía el email.
     *
     * @param email Email del usuario
     * @return true si se procesó (aunque no garantiza que el email exista por seguridad)
     */
    @Transactional
    public boolean initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Por seguridad, no revelamos si el email existe o no
            // Simulamos un pequeño delay para evitar timing attacks
            try {
                Thread.sleep(100 + new SecureRandom().nextInt(200));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return true;
        }

        User user = userOpt.get();

        // Verificar que el usuario esté activo
        if (!user.isEnabled() || user.isAccountLocked()) {
            return true; // No revelar estado de la cuenta
        }

        // Verificar que el usuario tenga autenticación local (no solo OAuth)
        if (user.getPassword() == null && !"LOCAL".equals(user.getAuthProvider())) {
            // Usuario de OAuth sin contraseña local - enviar email diferente
            emailService.sendOAuthUserPasswordResetEmail(user.getEmail(), user.getAuthProvider());
            return true;
        }

        // Invalidar tokens anteriores
        tokenRepository.invalidateAllTokensForUser(user);

        // Generar nuevo token
        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setTokenHash(tokenHash);
        resetToken.setUser(user);
        resetToken.setExpiresAt(Instant.now().plusSeconds(tokenExpirationMinutes * 60L));
        resetToken.setCreatedAt(Instant.now());

        tokenRepository.save(resetToken);

        // Enviar email con el link de recuperación
        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink, tokenExpirationMinutes);

        return true;
    }

    /**
     * Valida un token de reset sin consumirlo
     */
    public boolean validateToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        return tokenRepository.findByTokenHash(tokenHash)
                .map(PasswordResetToken::isValid)
                .orElse(false);
    }

    /**
     * Restablece la contraseña usando un token válido
     */
    @Transactional
    public ResetResult resetPassword(String rawToken, String newPassword) {
        String tokenHash = hashToken(rawToken);

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHash(tokenHash);

        if (tokenOpt.isEmpty()) {
            return ResetResult.INVALID_TOKEN;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isUsed()) {
            return ResetResult.TOKEN_ALREADY_USED;
        }

        if (resetToken.isExpired()) {
            return ResetResult.TOKEN_EXPIRED;
        }

        User user = resetToken.getUser();

        // Verificar que el usuario sigue activo
        if (!user.isEnabled() || user.isAccountLocked()) {
            return ResetResult.USER_INACTIVE;
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // Si era usuario OAuth, ahora también tiene auth local
        if (!"LOCAL".equals(user.getAuthProvider())) {
            // Mantener el provider OAuth pero ahora tiene contraseña
        }

        userRepository.save(user);

        // Marcar token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Enviar email de confirmación
        emailService.sendPasswordChangedConfirmation(user.getEmail(), user.getName());

        return ResetResult.SUCCESS;
    }

    /**
     * Limpia tokens expirados (para ejecutar periódicamente)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(Instant.now());
    }

    /**
     * Genera un token seguro de 32 bytes codificado en Base64 URL-safe
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Hashea el token con SHA-256 para almacenamiento seguro
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public enum ResetResult {
        SUCCESS,
        INVALID_TOKEN,
        TOKEN_EXPIRED,
        TOKEN_ALREADY_USED,
        USER_INACTIVE
    }
}
