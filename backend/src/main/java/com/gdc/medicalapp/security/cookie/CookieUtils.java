package com.gdc.medicalapp.security.cookie;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtils {

    public static ResponseCookie accessToken(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false) // true en producción (HTTPS)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();
    }

    public static ResponseCookie refreshToken(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();
    }

    public static ResponseCookie delete(String name) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build();
    }
}
