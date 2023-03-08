package ru.practicum.ewm.main_service.participate_request.util;

public enum RequestStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELED("CANCELED"),
    REJECTED("REJECTED");

    private final String state;

    private RequestStatus(String state) {
        this.state = state;
    }

    public String toString() {
        return state;
    }
}
