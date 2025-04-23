package com.example.FinanceApp1.repository;

import com.example.FinanceApp1.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.FinanceApp1.model.AppUser;


public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByAssignedUser(AppUser user);

    // Поиск задач по названию
    List<Request> findByTitleContainingIgnoreCase(String title);

    // Поиск задач по статусу
    List<Request> findByStatus(String status);

}
