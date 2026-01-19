package com.gdc.medicalapp.controllers.auth.dto;

public record LoginRequest(
        String emailOrUsername,
        String password
) {}

