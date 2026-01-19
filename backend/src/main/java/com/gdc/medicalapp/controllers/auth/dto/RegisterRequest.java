package com.gdc.medicalapp.controllers.auth.dto;

public record RegisterRequest(
        String email,
        String username,
        String password,
        String name
) {}
