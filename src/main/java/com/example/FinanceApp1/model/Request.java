package com.example.FinanceApp1.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;
    private String priority;

    @Enumerated(EnumType.STRING)
    private RequestType type; // Тип заявки

    private LocalDate createdDate;
    private LocalDate dueDate;
    private boolean confirmed;

    @ManyToOne
    private AppUser assignedUser;

    public Request() {
    }

    public Request(String title, String description, String status, String priority,
                   RequestType type, LocalDate createdDate, LocalDate dueDate,
                   boolean confirmed, AppUser assignedUser) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.type = type;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.confirmed = confirmed;
        this.assignedUser = assignedUser;
    }

    // Геттеры и сеттеры


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public AppUser getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(AppUser assignedUser) {
        this.assignedUser = assignedUser;
    }


}




