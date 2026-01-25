package com.gdc.medicalapp.security.cookie;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtils {

    public static ResponseCookie accessToken(String token, long expirationMillis) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false) // true en producción (HTTPS)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMillis(expirationMillis))
                .build();
    }

    public static ResponseCookie refreshToken(String token, long expirationMillis) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofMillis(expirationMillis))
                .build();
    }

    public static ResponseCookie delete(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path(path)
                .maxAge(0)
                .build();
    }
}
