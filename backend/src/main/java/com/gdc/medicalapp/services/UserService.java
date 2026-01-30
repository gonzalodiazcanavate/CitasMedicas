package com.gdc.medicalapp.services;

import com.gdc.medicalapp.controllers.auth.dto.RegisterRequest;
import com.gdc.medicalapp.domain.entities.User;
import com.gdc.medicalapp.domain.enums.UserRole;
import com.gdc.medicalapp.repositories.UserRepository;
import com.gdc.medicalapp.security.user.UserPrincipal;
import com.gdc.medicalapp.services.oauth.GoogleTokenVerifierService.GoogleUserInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        user.setAuthProvider("LOCAL");

        // 🔐 HASH DE PASSWORD
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        return userRepository.save(user);
    }

    /**
     * Procesa el login con Google. Solo permite login a usuarios existentes.
     * No se permite el registro de nuevos usuarios con Google por motivos de seguridad.
     *
     * @throws GoogleUserNotFoundException si el usuario no existe en el sistema
     */
    @Transactional
    public User processGoogleLogin(GoogleUserInfo googleUserInfo) {
        // 1. Buscar por Google ID (usuario ya vinculó su cuenta de Google)
        return userRepository.findByGoogleId(googleUserInfo.googleId())
                .map(user -> {
                    // Usuario existente con Google vinculado
                    validateUserActive(user);
                    return user;
                })
                .orElseGet(() -> {
                    // 2. Buscar por email (usuario existe pero no ha vinculado Google)
                    return userRepository.findByEmail(googleUserInfo.email())
                            .map(existingUser -> {
                                // Vincular Google ID a usuario existente
                                existingUser.setGoogleId(googleUserInfo.googleId());
                                // Opcional: actualizar nombre si no tiene
                                if (existingUser.getName() == null || existingUser.getName().isBlank()) {
                                    existingUser.setName(googleUserInfo.name());
                                }
                                validateUserActive(existingUser);
                                return userRepository.save(existingUser);
                            })
                            .orElseThrow(() -> new GoogleUserNotFoundException(
                                    "Por motivos de seguridad, el inicio de sesión con Google solo está disponible " +
                                    "para usuarios ya registrados. Por favor, regístrese primero con email y contraseña."
                            ));
                });
    }

    /**
     * Excepción lanzada cuando un usuario intenta hacer login con Google pero no existe en el sistema
     */
    public static class GoogleUserNotFoundException extends RuntimeException {
        public GoogleUserNotFoundException(String message) {
            super(message);
        }
    }

    private void validateUserActive(User user) {
        if (!user.isEnabled()) {
            throw new IllegalStateException("La cuenta está deshabilitada");
        }
        if (user.isAccountLocked()) {
            throw new IllegalStateException("La cuenta está bloqueada");
        }
    }

    public UserPrincipal loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado con id: " + id)
                );

        return new UserPrincipal(user);
    }

    public boolean isUserActive(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.isEnabled() && !user.isAccountLocked())
                .orElse(false);
    }
}