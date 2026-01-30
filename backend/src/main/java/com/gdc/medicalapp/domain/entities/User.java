package com.gdc.medicalapp.domain.entities;

import com.gdc.medicalapp.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String name;

    // Opcional: Control de estado del usuario
    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountLocked = false;

    // OAuth2: Google ID para usuarios que se autentican con Google
    @Column(unique = true)
    private String googleId;

    // Indica el proveedor de autenticación (LOCAL, GOOGLE, etc.)
    @Column(nullable = false)
    private String authProvider = "LOCAL";
}
