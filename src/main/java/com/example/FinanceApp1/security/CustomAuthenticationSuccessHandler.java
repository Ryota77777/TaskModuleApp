package com.example.FinanceApp1.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        System.out.println("Пользователь успешно аутентифицирован: " + authentication.getName());
        System.out.println("Роли пользователя:");

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            System.out.println(" - " + authority.getAuthority());
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin");
                return;
            } else if (role.equals("ROLE_USER")) {
                response.sendRedirect("/home");
                return;
            }
        }
        response.sendRedirect("/default");
    }

}

