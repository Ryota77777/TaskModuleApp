package com.example.FinanceApp1.controller;

import com.example.FinanceApp1.model.AppUser;
import com.example.FinanceApp1.model.Request;
import com.example.FinanceApp1.service.AuthService;
import com.example.FinanceApp1.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RequestController {

    private final RequestService requestService;
    private final AuthService authService;

    @Autowired
    public RequestController(RequestService requestService, AuthService authService) {
        this.requestService = requestService;
        this.authService = authService;
    }

    // Все задачи пользователя
    @GetMapping("/requests")
    public String userRequests(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = authService.findUserByUsername(auth.getName());
        model.addAttribute("requests", requestService.getRequestsByUser(user));
        return "requests";
    }

    @PostMapping("/requests/update")
    public String updateRequest(@RequestParam Long id,
                                @RequestParam String title,
                                @RequestParam String description,
                                @RequestParam(required = false) String status,  // Параметр status теперь необязателен
                                @RequestParam String priority,
                                @RequestParam String type,
                                @RequestParam String dueDate,
                                Model model) {
        // Логика обработки
        if (status == null) {
            // Если status не передан, установим значение по умолчанию
            status = "default"; // например, дефолтный статус
        }
        requestService.updateRequest(id, title, description, status, priority, type, dueDate);
        return "redirect:/requests";
    }

    @GetMapping("/manage")
    public String manageRequests(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = authService.findUserByUsername(auth.getName());

        List<Request> requests = requestService.getRequestsByUser(user);
        if (requests == null) {
            requests = new ArrayList<>();
        }

        model.addAttribute("requests", requests);
        return "manage";
    }

    @GetMapping("/requests/search/title")
    public String searchRequestsByTitle(@RequestParam String title, Model model) {
        List<Request> requests = requestService.searchRequestsByTitle(title);
        model.addAttribute("requests", requests);
        return "requests"; // Переход к странице с результатами поиска
    }

    // Поиск задач по статусу
    @GetMapping("/requests/search/status")
    public String searchRequestsByStatus(@RequestParam String status, Model model) {
        List<Request> requests = requestService.searchRequestsByStatus(status);
        model.addAttribute("requests", requests);
        return "requests"; // Переход к странице с результатами поиска
    }

    @PostMapping("/manage/add")
    public String addRequest(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String priority,
                             @RequestParam String type,
                             @RequestParam String dueDate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = authService.findUserByUsername(auth.getName());
        requestService.createRequest(title, description, priority, type, dueDate, user);
        return "redirect:/manage";
    }


    @PostMapping("/manage/delete/{id}")
    public String deleteRequest(@PathVariable Long id) {
        requestService.deleteRequestById(id);
        return "redirect:/manage";
    }

    @PostMapping("/manage/complete/{id}")
    public String completeRequest(@PathVariable Long id) {
        requestService.markAsCompleted(id);
        return "redirect:/manage";
    }

    // Редактирование задачи
    @GetMapping("/manage/edit/{id}")
    public String editRequest(@PathVariable Long id, Model model) {
        Request request = requestService.getRequestById(id);
        model.addAttribute("request", request);
        return "edit_request"; // Новый шаблон для редактирования задачи
    }

    @PostMapping("/manage/edit/{id}")
    public String updateRequest(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String status,
                             @RequestParam String priority,
                             @RequestParam String type,
                             @RequestParam String dueDate) {  // И передаем dueDate
        requestService.updateRequest(id, title, description, status, priority, type, dueDate);
        return "redirect:/manage";
    }


    @GetMapping("/analytics")
    public String analyticsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = authService.findUserByUsername(auth.getName());

        // Получение данных для аналитики
        Map<String, Long> statusDistribution = requestService.getStatusDistribution(user);
        Map<String, Long> typeDistribution = requestService.getTypeDistribution(user);
        Map<String, Long> priorityDistribution = requestService.getPriorityDistribution(user);
        Map<LocalDate, Long> dailyRequestCount = requestService.getDailyRequestCount(user, 30);
        List<Request> upcomingDueRequests = requestService.getUpcomingDueRequests(user, 7);

        // Преобразуем Map в List для удобства работы в шаблоне
        model.addAttribute("dailyLabels", new ArrayList<>(dailyRequestCount.keySet()));
        model.addAttribute("dailyData", new ArrayList<>(dailyRequestCount.values()));

        // Передача других данных в модель
        model.addAttribute("statusDistribution", statusDistribution);
        model.addAttribute("typeDistribution", typeDistribution);
        model.addAttribute("priorityDistribution", priorityDistribution);
        model.addAttribute("upcomingDueRequests", upcomingDueRequests);

        return "analytics";
    }
}

