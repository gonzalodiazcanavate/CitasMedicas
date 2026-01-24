package com.gdc.medicalapp.controllers.auth;

import com.gdc.medicalapp.controllers.auth.dto.LoginRequest;
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
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) {

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
        // Persistir refresh token
        refreshTokenService.create(user.getUser(), refreshToken);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.accessToken(accessToken).toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.refreshToken(refreshToken).toString()
                )
                .build();
    }

    /* Refresh Token */

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).build();
        }
        // Comprobar que el refresh token existe en BD
        RefreshToken stored = refreshTokenService.verify(refreshToken);

        User user = stored.getUser();
        UserPrincipal principal = new UserPrincipal(user);

        String newAccessToken = jwtService.generateAccessToken(principal);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.accessToken(newAccessToken).toString()
                )
                .build();
    }

    /* Logout */

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {

        if (refreshToken != null) {
            refreshTokenService.verify(refreshToken);
        }
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.delete("access_token").toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.delete("refresh_token").toString()
                )
                .build();
    }
}