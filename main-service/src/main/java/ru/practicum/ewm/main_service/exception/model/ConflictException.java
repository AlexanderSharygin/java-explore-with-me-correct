package ru.practicum.ewm.main_service.exception.model;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}