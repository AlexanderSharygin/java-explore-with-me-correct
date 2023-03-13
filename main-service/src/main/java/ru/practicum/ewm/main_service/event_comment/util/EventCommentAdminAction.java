package ru.practicum.ewm.main_service.event_comment.util;

public enum EventCommentAdminAction {
    ACCEPT("ACCEPT"),
    REJECT("REJECT");

    private final String action;

    EventCommentAdminAction(String action) {
        this.action = action;
    }

    public String toString() {
        return action;
    }
}