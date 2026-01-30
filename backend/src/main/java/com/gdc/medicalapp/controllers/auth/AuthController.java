package com.gdc.medicalapp.controllers.auth;

import com.gdc.medicalapp.controllers.auth.dto.AppleLoginRequest;
import com.gdc.medicalapp.controllers.auth.dto.GoogleLoginRequest;
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
import com.gdc.medicalapp.services.oauth.AppleTokenVerifierService;
import com.gdc.medicalapp.services.oauth.AppleTokenVerifierService.AppleUserInfo;
import com.gdc.medicalapp.services.oauth.GoogleTokenVerifierService;
import com.gdc.medicalapp.services.oauth.GoogleTokenVerifierService.GoogleUserInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.time.Instant;

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
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final AppleTokenVerifierService appleTokenVerifierService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            RefreshTokenService refreshTokenService,
            GoogleTokenVerifierService googleTokenVerifierService,
            AppleTokenVerifierService appleTokenVerifierService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.appleTokenVerifierService = appleTokenVerifierService;
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

        // Determinar duración del refresh token según "Remember Me"
        boolean rememberMe = request.rememberMe() != null && request.rememberMe();
        long refreshTokenExpiration = rememberMe 
        ? jwtService.getExtendedRefreshTokenExpiration()  // 30 días
        : jwtService.getRefreshTokenExpiration();         // 7 días

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenExpiration);

        // Limpiar tokens expirados del usuario antes de crear uno nuevo
        refreshTokenService.deleteExpiredTokensByUser(user.getUser());

        // Persistir refresh token
        refreshTokenService.create(user.getUser(), refreshToken, refreshTokenExpiration);

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
                                refreshTokenExpiration
                        ).toString()
                )
                .body(body);
    }

    /* Google Login */

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request
    ) {
        try {
            // 1. Verificar el token de Google
            GoogleUserInfo googleUserInfo = googleTokenVerifierService.verifyToken(request.idToken());

            // 2. Buscar o crear usuario
            User user = userService.processGoogleLogin(googleUserInfo);

            // 3. Crear UserPrincipal para generar JWT
            UserPrincipal principal = new UserPrincipal(user);

            // 4. Generar tokens (usamos duración extendida por defecto para OAuth)
            long refreshTokenExpiration = jwtService.getExtendedRefreshTokenExpiration();
            String accessToken = jwtService.generateAccessToken(principal);
            String refreshToken = jwtService.generateRefreshToken(principal, refreshTokenExpiration);

            // 5. Limpiar tokens expirados y persistir nuevo refresh token
            refreshTokenService.deleteExpiredTokensByUser(user);
            refreshTokenService.create(user, refreshToken, refreshTokenExpiration);

            // 6. Preparar respuesta
            LoginResponse body = new LoginResponse(UserDto.from(user));

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
                                    refreshTokenExpiration
                            ).toString()
                    )
                    .body(body);

        } catch (GoogleTokenVerifierService.InvalidGoogleTokenException e) {
            return ResponseEntity.status(401).build();
        } catch (UserService.GoogleUserNotFoundException e) {
            // Usuario no registrado intentando hacer login con Google
            return ResponseEntity.status(404)
                    .body(new LoginResponse(null, e.getMessage()));
        } catch (IllegalStateException e) {
            // Usuario deshabilitado o bloqueado
            return ResponseEntity.status(403).build();
        }
    }

    /* Apple Login */

    @PostMapping("/apple")
    public ResponseEntity<LoginResponse> appleLogin(
            @Valid @RequestBody AppleLoginRequest request
    ) {
        try {
            // 1. Verificar el token de Apple (incluye nombre si es primer login)
            AppleUserInfo appleUserInfo = appleTokenVerifierService.verifyToken(
                    request.idToken(),
                    request.firstName(),
                    request.lastName()
            );

            // 2. Buscar usuario existente
            User user = userService.processAppleLogin(appleUserInfo);

            // 3. Crear UserPrincipal para generar JWT
            UserPrincipal principal = new UserPrincipal(user);

            // 4. Generar tokens (usamos duración extendida por defecto para OAuth)
            long refreshTokenExpiration = jwtService.getExtendedRefreshTokenExpiration();
            String accessToken = jwtService.generateAccessToken(principal);
            String refreshToken = jwtService.generateRefreshToken(principal, refreshTokenExpiration);

            // 5. Limpiar tokens expirados y persistir nuevo refresh token
            refreshTokenService.deleteExpiredTokensByUser(user);
            refreshTokenService.create(user, refreshToken, refreshTokenExpiration);

            // 6. Preparar respuesta
            LoginResponse body = new LoginResponse(UserDto.from(user));

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
                                    refreshTokenExpiration
                            ).toString()
                    )
                    .body(body);

        } catch (AppleTokenVerifierService.InvalidAppleTokenException e) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(null, e.getMessage()));
        } catch (UserService.AppleUserNotFoundException e) {
            // Usuario no registrado intentando hacer login con Apple
            return ResponseEntity.status(404)
                    .body(new LoginResponse(null, e.getMessage()));
        } catch (IllegalStateException e) {
            // Usuario deshabilitado o bloqueado
            return ResponseEntity.status(403).build();
        }
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

            // Extraemos el tiempo restante para expiración de StoredToken para añadirlo a nuevo token (Absolute Expiration)
            long storedTokenExpiration = storedToken.getExpiresAt().toEpochMilli() - java.time.Instant.now().toEpochMilli();
            // ROTACIÓN DE TOKENS: Generar nuevo access y refresh token
            String newAccessToken = jwtService.generateAccessToken(principal);
            String newRefreshToken = jwtService.generateRefreshToken(principal, storedTokenExpiration);

            // Revocar el token antiguo (previene reutilización)
            refreshTokenService.revoke(storedToken);

            // Crear nuevo refresh token con la misma fecha de expiración que el anterior
            refreshTokenService.create(user, newRefreshToken, storedTokenExpiration);

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
                                    storedTokenExpiration
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

