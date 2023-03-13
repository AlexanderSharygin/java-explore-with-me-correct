package ru.practicum.ewm.main_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.ewm.client",
        "ru.practicum.ewm.main_service.compilation",
        "ru.practicum.ewm.main_service.event",
        "ru.practicum.ewm.main_service.event_category",
        "ru.practicum.ewm.main_service.exception",
        "ru.practicum.ewm.main_service.location",
        "ru.practicum.ewm.main_service.participate_request",
        "ru.practicum.ewm.main_service.user", "ru.practicum.ewm.main_service.event_comment"})
public class EwmMainService {
    public static void main(String[] args) {
        SpringApplication.run(EwmMainService.class, args);
    }
}