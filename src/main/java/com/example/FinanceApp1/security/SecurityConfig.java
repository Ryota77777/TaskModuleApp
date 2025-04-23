package com.example.FinanceApp1.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Создание PasswordEncoder с логированием
    @Bean
    public PasswordEncoder passwordEncoder() {
        try {
            logger.info("Создан бин PasswordEncoder (BCrypt)");
            return new BCryptPasswordEncoder();
        } catch (Exception e) {
            logger.error("Ошибка при создании PasswordEncoder: {}", e.getMessage());
            throw new RuntimeException("Ошибка при создании PasswordEncoder", e);  // Прокидываем исключение дальше
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                    .authorizeHttpRequests((requests) -> requests
                            .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")  // ← доступ только админу
                            .anyRequest().authenticated() // все остальные маршруты доступны только авторизованным
                    )

                    .formLogin((form) -> form
                            .loginPage("/login")
                            .defaultSuccessUrl("/profile", true)  // Переадресация на профиль после успешного логина
                            .permitAll()
                    )
                    .logout((logout) -> logout
                            .logoutSuccessUrl("/login?logout")
                            .permitAll()
                    )
                    .sessionManagement((session) -> session
                            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)  // Всегда создавать сессию
                    )
                    .userDetailsService(customUserDetailsService);  // Указываем наш кастомный сервис

            logger.info("Конфигурация безопасности успешно применена.");
            return http.build();
        } catch (Exception e) {
            logger.error("Ошибка при настройке безопасности: {}", e.getMessage());
            throw new RuntimeException("Ошибка при настройке безопасности", e);  // Прокидываем исключение дальше
        }
    }
}



















