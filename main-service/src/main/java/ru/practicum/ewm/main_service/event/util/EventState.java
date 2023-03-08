package ru.practicum.ewm.main_service.event.util;

public enum EventState {

    PENDING("PENDING"),
    PUBLISHED("PUBLISHED"),
    CANCELED("CANCELED");

    private final String state;

    private EventState(String state) {
        this.state = state;
    }

    public String toString() {
        return state;
    }
}
