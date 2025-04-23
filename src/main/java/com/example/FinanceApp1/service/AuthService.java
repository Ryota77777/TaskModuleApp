package com.example.FinanceApp1.service;

import com.example.FinanceApp1.model.AppUser;
import com.example.FinanceApp1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Метод для аутентификации пользователя
    public AppUser authenticateUser(String username, String password) {
        try {
            logger.info("Попытка входа пользователя: {}", username);
            AppUser user = userRepository.findByUsername(username);

            if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                logger.info("Аутентификация успешна для пользователя: {}", username);
                return user;
            } else {
                logger.warn("Аутентификация не удалась для пользователя: {}", username);
            }
        } catch (Exception e) {
            logger.error("Ошибка при аутентификации пользователя {}: {}", username, e.getMessage());
            throw new RuntimeException("Ошибка при аутентификации пользователя", e);
        }
        return null;
    }

    // Метод для регистрации нового пользователя
    public AppUser registerUser(String username, String password, String email) {
        try {
            logger.info("Регистрация пользователя: {}", username);
            AppUser newUser = new AppUser();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setEmail(email);
            newUser.setRole("USER");


            AppUser savedUser = userRepository.save(newUser);
            logger.info("Пользователь {} успешно зарегистрирован с ID: {}", username, savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя {}: {}", username, e.getMessage());
            throw new RuntimeException("Ошибка при регистрации пользователя", e);
        }
    }

    // Метод для поиска пользователя по имени
    public AppUser findUserByUsername(String username) {
        try {
            logger.debug("Поиск пользователя по имени: {}", username);
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя {}: {}", username, e.getMessage());
            throw new RuntimeException("Ошибка при поиске пользователя", e);
        }
    }

    public void updateUser(AppUser user) {
        userRepository.save(user); // просто перезаписывает юзера
    }
}












