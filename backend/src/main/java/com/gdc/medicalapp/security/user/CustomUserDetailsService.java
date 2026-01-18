package com.gdc.medicalapp.security.user;

import com.gdc.medicalapp.domain.entities.User;
import com.gdc.medicalapp.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * emailOrUsername = lo que el usuario ha escrito en el login
     */
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmailOrUsername(emailOrUsername)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + emailOrUsername
                        )
                );
        return new UserPrincipal(user);
    }
}