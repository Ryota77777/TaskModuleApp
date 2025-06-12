package com.example.FinanceApp1.repository;

import com.example.FinanceApp1.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);


}




