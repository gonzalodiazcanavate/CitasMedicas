package com.gdc.medicalapp.config.security;

import com.gdc.medicalapp.security.jwt.JwtAuthenticationFilter;
import com.gdc.medicalapp.security.user.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /* Hash de contraseñas */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* Proveedor de autenticación */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /* AuthenticationManager */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /* Filtros de seguridad */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // No CSRF (lo retomaremos luego)
                .csrf(csrf -> csrf.disable())

                // No Sesiones (solo JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // REGISTRAR EL PROVIDER
                .authenticationProvider(authenticationProvider())

                // Filtro JWT
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // Reglas de autorización
                .authorizeHttpRequests(auth -> auth
                       .requestMatchers(
                            "/auth/login",
                            "/auth/google",
                            "/auth/apple",
                            "/auth/refresh",
                            "/auth/logout",
                            "/auth/register",
                            "/auth/forgot-password",
                            "/auth/reset-password",
                            "/auth/validate-reset-token",
                            "/test/me"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Sin Login por formulario
                .formLogin(form -> form.disable())

                // Sin HTTP Basic
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
