package com.example.FinanceApp1.security;

import com.example.FinanceApp1.repository.UserRepository;
import com.example.FinanceApp1.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final UserRepository userRepository;

    public DataInitializer(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "admin";
        if (userRepository.findByUsername(adminUsername) == null) {
            // создаём админа с заранее заданным паролем (например, "admin123")
            authService.registerAdmin(adminUsername, "admin123", "admin@example.com");
        }
    }
}
