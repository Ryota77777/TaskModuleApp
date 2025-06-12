package com.example.FinanceApp1.controller;

import com.example.FinanceApp1.config.FileStorageService;
import com.example.FinanceApp1.model.AppUser;
import com.example.FinanceApp1.model.Request;
import com.example.FinanceApp1.service.AuthService;
import com.example.FinanceApp1.service.RequestService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RequestController {

    private final RequestService requestService;
    private final AuthService authService;
    @Value("${app.upload.dir}")
    private String uploadDir;
    @Autowired
    private FileStorageService fileStorageService;


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
                                @RequestParam String filePath,
                                Model model) {
        // Логика обработки
        if (status == null) {
            // Если status не передан, установим значение по умолчанию
            status = "default"; // например, дефолтный статус
        }
        requestService.updateRequest(id, title, description, status, priority, type, dueDate, filePath);
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
                             @RequestParam String dueDate,
                             @RequestParam(value = "file", required = false) MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = authService.findUserByUsername(auth.getName());

        String filePath = null;
        if (file != null && !file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(uploadDir); // заменено на переменную
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                file.transferTo(uploadPath.resolve(filename).toFile());
                filePath = filename;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        requestService.createRequest(title, description, priority, type, dueDate, user, filePath);
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
    @PostMapping("/manage/edit/{id}")
    public String updateRequest(@PathVariable Long id,
                                @RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String status,
                                @RequestParam String priority,
                                @RequestParam String type,
                                @RequestParam String dueDate,
                                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        // Если файл был загружен, обработать его
        if (file != null && !file.isEmpty()) {
            // Логика сохранения файла (например, на диск или в хранилище)
            String savedFilePath = fileStorageService.saveFile(file);

            // Передать путь сохраненного файла в сервис
            requestService.updateRequest(id, title, description, status, priority, type, dueDate, savedFilePath);
        } else {
            // Если файл не загружен, обновить заявку без изменения файла
            requestService.updateRequest(id, title, description, status, priority, type, dueDate, null);
        }

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

    @GetMapping("/download")
    public void downloadFile(@RequestParam("path") String path, HttpServletResponse response) throws IOException {
        Path file = Paths.get(uploadDir).resolve(path); // заменено на переменную

        if (!Files.exists(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Файл не найден");
            return;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName().toString() + "\"");
        Files.copy(file, response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("/requests/by-user")
    public String getRequestsByUser(@RequestParam(required = false) Long userId, Model model) {
        List<Request> requests;
        if (userId != null) {
            requests = requestService.getRequestsByAssignedUser(userId);
        } else {
            requests = requestService.getAllRequests();
        }

        model.addAttribute("users", authService.getAllUsers()); // все юзеры
        model.addAttribute("requests", requests);
        model.addAttribute("selectedUserId", userId);
        return "requests_by_users";
    }

    @PostMapping("/requests/change-user")
    public String changeAssignedUser(@RequestParam Long requestId, @RequestParam(required = false) Long newUserId) {
        requestService.changeAssignedUser(requestId, newUserId);
        return "redirect:/requests/by-user";
    }






}

