package com.gdc.medicalapp.controllers.auth;

import com.gdc.medicalapp.controllers.auth.dto.LoginRequest;
import com.gdc.medicalapp.controllers.auth.dto.RegisterRequest;
import com.gdc.medicalapp.security.cookie.CookieUtils;
import com.gdc.medicalapp.security.jwt.JwtService;
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

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest request
    ) {
        userService.register(request);
        return ResponseEntity.ok().build();
    }


    /**
     * LOGIN
     */
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

    /**
     * LOGOUT
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

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
