package com.gdc.medicalapp.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarea programada para limpieza periódica de tokens de actualización
 * expirados y revocados de la base de datos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupTask {

    private final RefreshTokenService refreshTokenService;

    /**
     * Ejecuta limpieza de tokens expirados cada día a las 3:00 AM.
     * Los tokens revocados se mantienen por 7 días como audit trail.
     */
    @Scheduled(cron = "0 6 3 * * ?")
    public void cleanupExpiredTokens() {
        log.info("Iniciando limpieza de refresh tokens expirados...");
        
        try {
            refreshTokenService.deleteAllExpiredTokens();
            log.info("Limpieza de refresh tokens completada exitosamente");
        } catch (Exception e) {
            log.error("Error durante limpieza de refresh tokens: {}", e.getMessage(), e);
        }
    }
}
