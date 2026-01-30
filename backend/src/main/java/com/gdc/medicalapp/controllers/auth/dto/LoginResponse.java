package com.gdc.medicalapp.controllers.auth.dto;

public record LoginResponse(
        UserDto user,
        String message
) {
    // Constructor para login exitoso (sin mensaje)
    public LoginResponse(UserDto user) {
        this(user, null);
    }
}
