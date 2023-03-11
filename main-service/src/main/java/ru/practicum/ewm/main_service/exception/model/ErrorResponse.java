package ru.practicum.ewm.main_service.exception.model;

import lombok.Data;

@Data
public class ErrorResponse {
    String error;

    String status;
    String description;

    public ErrorResponse(String error, String description, String status) {
        this.error = error;
        this.description = description;
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}