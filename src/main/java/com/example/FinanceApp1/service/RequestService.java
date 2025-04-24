package com.example.FinanceApp1.service;

import com.example.FinanceApp1.model.AppUser;
import com.example.FinanceApp1.model.Request;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RequestService {
    List<Request> getAllRequests();
    void deleteRequestById(Long id);
    List<Request> getRequestsByUser(AppUser user);
    void createRequest(String title, String description, String priority, String type, String dueDate, AppUser user);

    void markAsCompleted(Long id);

    void updateRequest(Long id, String title, String description, String status, String priority, String type, String dueDate);

    Request getRequestById(Long id); // Добавляем метод для получения задачи по ID
    // Метод поиска задачи по названию
    List<Request> searchRequestsByTitle(String title);

    // Метод поиска задачи по статусу
    List<Request> searchRequestsByStatus(String status);

    Map<String, Long> getStatusDistribution(AppUser user);
    Map<String, Long> getTypeDistribution(AppUser user);
    Map<String, Long> getPriorityDistribution(AppUser user);
    Map<LocalDate, Long> getDailyRequestCount(AppUser user, int days);
    List<Request> getUpcomingDueRequests(AppUser user, int days);
}





