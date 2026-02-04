package com.gdc.medicalapp.controllers.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest(
        @NotBlank(message = "El token de Apple es requerido")
        String idToken,

        // Nombre del usuario (solo disponible en el primer login con Apple)
        String firstName,
        String lastName
) {}
