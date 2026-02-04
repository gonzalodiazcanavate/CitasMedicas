package com.gdc.medicalapp.controllers.auth.dto;

import com.gdc.medicalapp.domain.entities.User;

public record UserDto(
        Long id,
        String username,
        String email,
        String role
) {

    public static UserDto from(User user) {
        if (user == null) return null;
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }
}
