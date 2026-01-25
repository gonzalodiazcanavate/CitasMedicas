package com.gdc.medicalapp.controllers.auth;

import com.gdc.medicalapp.controllers.auth.dto.LoginRequest;
import com.gdc.medicalapp.controllers.auth.dto.LoginResponse;
import com.gdc.medicalapp.controllers.auth.dto.UserDto;
import com.gdc.medicalapp.controllers.auth.dto.RegisterRequest;
import com.gdc.medicalapp.domain.entities.RefreshToken;
import com.gdc.medicalapp.domain.entities.User;
import com.gdc.medicalapp.security.cookie.CookieUtils;
import com.gdc.medicalapp.security.jwt.JwtService;
import com.gdc.medicalapp.security.jwt.RefreshTokenService;
import com.gdc.medicalapp.security.user.UserPrincipal;
import com.gdc.medicalapp.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    /* Register */

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest request
    ) {
        userService.register(request);
        return ResponseEntity.ok().build();
    }


    /* Login */

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.emailOrUsername(),
                                request.password()
                        )
                );

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Limpiar tokens expirados del usuario antes de crear uno nuevo
        refreshTokenService.deleteExpiredTokensByUser(user.getUser());

        // Persistir refresh token
        refreshTokenService.create(user.getUser(), refreshToken);

        LoginResponse body = new LoginResponse(UserDto.from(user.getUser()));

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.accessToken(
                                accessToken,
                                jwtService.getAccessTokenExpiration()
                        ).toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.refreshToken(
                                refreshToken,
                                jwtService.getRefreshTokenExpiration()
                        ).toString()
                )
                .body(body);
    }

    /* Refresh Token */

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        if (!jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        try {
            // Comprobar que el refresh token existe en BD y no está revocado/expirado
            RefreshToken storedToken = refreshTokenService.verify(refreshToken);
            User user = storedToken.getUser();

            // Verificar que el usuario sigue activo
            if (!userService.isUserActive(user.getId())) {
                refreshTokenService.revoke(storedToken);
                return ResponseEntity.status(401).build();
            }

            UserPrincipal principal = new UserPrincipal(user);

            // ROTACIÓN DE TOKENS: Generar nuevo access y refresh token
            String newAccessToken = jwtService.generateAccessToken(principal);
            String newRefreshToken = jwtService.generateRefreshToken(principal);

            // Revocar el token antiguo (previene reutilización)
            refreshTokenService.revoke(storedToken);

            // Crear nuevo refresh token
            refreshTokenService.create(user, newRefreshToken);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.SET_COOKIE,
                            CookieUtils.accessToken(
                                    newAccessToken,
                                    jwtService.getAccessTokenExpiration()
                            ).toString()
                    )
                    .header(
                            HttpHeaders.SET_COOKIE,
                            CookieUtils.refreshToken(
                                    newRefreshToken,
                                    jwtService.getRefreshTokenExpiration()
                            ).toString()
                    )
                    .build();

        } catch (RuntimeException e) {
            // Token inválido, expirado o revocado
            return ResponseEntity.status(401).build();
        }
    }

    /* Logout */

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                refreshTokenService.revokeByToken(refreshToken);
            } catch (Exception e) {
                // Token ya no existe o es inválido, continuar con logout
            }
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.delete("access_token", "/").toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.delete("refresh_token", "/").toString()
                )
                .build();
    }
}

