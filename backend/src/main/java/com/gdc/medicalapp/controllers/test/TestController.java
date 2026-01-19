package com.gdc.medicalapp.controllers.test;

import com.gdc.medicalapp.security.user.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/me")
    public String me() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal user = (UserPrincipal) auth.getPrincipal();

        return "Hola " + user.getUsername()
                + " con rol " + user.getRole();
    }
}

