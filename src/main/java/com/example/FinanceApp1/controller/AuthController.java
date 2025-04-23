package com.example.FinanceApp1.controller;

import com.example.FinanceApp1.model.AppUser;
import com.example.FinanceApp1.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    private final AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;



    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }



    // Главная страница
    @GetMapping("/")
    public String index() {
        logger.info("User accessed the home page.");
        return "index";
    }

    // Страница логина
    @GetMapping("/login")
    public String login() {
        logger.info("User accessed the login page.");
        return "login";
    }


    // Страница регистрации
    @GetMapping("/register")
    public String register() {
        logger.info("User accessed the registration page.");
        return "register";
    }

    // Обработка регистрации
    @PostMapping("/register")
    public String registerPost(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email) {
        logger.info("User attempted to register with username: {}", username);
        try {
            AppUser user = authService.registerUser(username, password, email);
            authenticateSession(user.getUsername(), password);  // передаём сырой пароль
            // Авторизуем пользователя после регистрации
            logger.info("User registered successfully. Redirecting to home.");
            return "redirect:/home";
        } catch (Exception e) {
            logger.error("Error during registration attempt for username: {}. Error: {}", username, e.getMessage());
            return "register";  // В случае ошибки
        }
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = auth.getName();
        AppUser user = authService.findUserByUsername(username);
        model.addAttribute("user", user);

        return "home";
    }

    // Страница профиля
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = auth.getName();
        AppUser user = authService.findUserByUsername(username);

        if (user == null) {
            return "redirect:/login";
        }

        // Удалите строки, которые касаются форматирования даты
        model.addAttribute("user", user);

        return "profile";
    }


    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String email,
                                @RequestParam String fullName,
                                @RequestParam String position,
                                @RequestParam String phone,
                                @RequestParam String department,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String username = auth.getName();
        AppUser user = authService.findUserByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }

        user.setEmail(email);
        user.setFullName(fullName);
        user.setPosition(position);
        user.setPhone(phone);
        user.setDepartment(department);

        authService.updateUser(user); // создадим этот метод в сервисе

        model.addAttribute("user", user);
        model.addAttribute("success", true); // чтобы можно было показать сообщение об успехе

        return "profile";
    }

    private void authenticateSession(String username, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext(); // Очищает текущую сессию
        return "redirect:/login?logout"; // Редирект на страницу логина или на "/"
    }
}







