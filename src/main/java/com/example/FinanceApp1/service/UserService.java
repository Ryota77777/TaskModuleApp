package com.example.FinanceApp1.service;

import com.example.FinanceApp1.model.AppUser;
import java.util.List;

public interface UserService {
    List<AppUser> getAllUsers();
    void deleteUserById(Long id);
}

