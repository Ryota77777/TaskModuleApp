package com.example.FinanceApp1.service.impl;

import com.example.FinanceApp1.model.AppUser;
import com.example.FinanceApp1.model.Request;
import com.example.FinanceApp1.model.RequestType;
import com.example.FinanceApp1.repository.RequestRepository;
import com.example.FinanceApp1.repository.UserRepository;
import com.example.FinanceApp1.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    public void deleteRequestById(Long id) {
        requestRepository.deleteById(id);
    }

    @Override
    public List<Request> searchRequestsByTitle(String title) {
        return requestRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Request> searchRequestsByStatus(String status) {
        return requestRepository.findByStatus(status);
    }

    @Override
    public List<Request> getRequestsByUser(AppUser user) {
        return requestRepository.findByAssignedUser(user);
    }


    @Override
    public Request getRequestById(Long id) {
        Optional<Request> request = requestRepository.findById(id);
        return request.orElse(null); // Возвращаем задачу или null, если не найдено
    }

    @Override
    public void createRequest(String title, String description, String priority, String type, String dueDate, AppUser user, String filePath) {
        LocalDate parsedDueDate = LocalDate.parse(dueDate);
        LocalDate createdDate = LocalDate.now();

        RequestType requestType = RequestType.valueOf(type);

        Request request = new Request(
                title,
                description,
                "NEW",
                priority,
                requestType,
                createdDate,
                parsedDueDate,
                false,
                user,
                filePath // ← новое поле
        );

        requestRepository.save(request);
    }

    @Override
    public void markAsCompleted(Long id) {
        Optional<Request> request = requestRepository.findById(id);
        request.ifPresent(r -> {
            r.setStatus("COMPLETED");
            r.setConfirmed(true); // можно сразу пометить как подтверждённую
            requestRepository.save(r);
        });
    }


    @Override
    public void updateRequest(Long id, String title, String description, String status, String priority, String type, String dueDate, String filePath) {
        Optional<Request> request = requestRepository.findById(id);
        request.ifPresent(r -> {
            r.setTitle(title);
            r.setDescription(description);
            r.setStatus(status);
            r.setPriority(priority);
            r.setType(RequestType.valueOf(type));
            r.setDueDate(LocalDate.parse(dueDate));
            r.setFilePath(filePath); // ← обновляем путь к файлу
            requestRepository.save(r);
        });
    }

    @Override
    public Map<String, Long> getStatusDistribution(AppUser user) {
        return requestRepository.findByAssignedUser(user).stream()
                .collect(Collectors.groupingBy(Request::getStatus, Collectors.counting()));
    }

    @Override
    public Map<String, Long> getTypeDistribution(AppUser user) {
        return requestRepository.findByAssignedUser(user).stream()
                .collect(Collectors.groupingBy(r -> r.getType().name(), Collectors.counting()));
    }

    @Override
    public Map<String, Long> getPriorityDistribution(AppUser user) {
        return requestRepository.findByAssignedUser(user).stream()
                .collect(Collectors.groupingBy(Request::getPriority, Collectors.counting()));
    }

    @Override
    public Map<LocalDate, Long> getDailyRequestCount(AppUser user, int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        return requestRepository.findByAssignedUser(user).stream()
                .filter(r -> r.getCreatedDate().isAfter(fromDate))
                .collect(Collectors.groupingBy(Request::getCreatedDate, Collectors.counting()));
    }

    @Override
    public List<Request> getUpcomingDueRequests(AppUser user, int days) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(days);
        return requestRepository.findByAssignedUser(user).stream()
                .filter(r -> !r.getStatus().equalsIgnoreCase("COMPLETED"))
                .filter(r -> r.getDueDate() != null && !r.getDueDate().isBefore(today) && !r.getDueDate().isAfter(limit))
                .collect(Collectors.toList());
    }

    @Override
    public List<Request> getRequestsByAssignedUser(Long userId) {
        AppUser user = userRepository.findById(userId).orElse(null);
        return requestRepository.findByAssignedUser(user);
    }

    @Override
    public void changeAssignedUser(Long requestId, Long newUserId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        if (newUserId != null) {
            AppUser newUser = userRepository.findById(newUserId).orElseThrow();
            request.setAssignedUser(newUser);
        } else {
            request.setAssignedUser(null); // снимаем исполнителя
        }
        requestRepository.save(request);
    }



}




