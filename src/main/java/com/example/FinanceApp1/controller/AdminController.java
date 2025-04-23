package com.example.FinanceApp1.controller;

import com.example.FinanceApp1.service.RequestService;
import com.example.FinanceApp1.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RequestService requestService;

    public AdminController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("requests", requestService.getAllRequests());
        return "admin"; // Thymeleaf шаблон
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    @PostMapping("/deleteRequest/{id}")
    public String deleteRequest(@PathVariable Long id) {
        requestService.deleteRequestById(id);
        return "redirect:/admin";
    }
}

