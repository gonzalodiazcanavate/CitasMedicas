package com.gdc.medicalapp.services;

import com.gdc.medicalapp.controllers.auth.dto.RegisterRequest;
import com.gdc.medicalapp.domain.entities.User;
import com.gdc.medicalapp.domain.enums.UserRole;
import com.gdc.medicalapp.repositories.UserRepository;
import com.gdc.medicalapp.security.user.UserPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email ya registrado");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username ya registrado");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setName(request.name());
        user.setRole(UserRole.PATIENT);

        // 🔐 HASH DE PASSWORD
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        return userRepository.save(user);
    }

    public UserPrincipal loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado con id: " + id)
                );

        return new UserPrincipal(user);
    }

}