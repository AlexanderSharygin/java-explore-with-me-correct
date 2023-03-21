package ru.practicum.ewm.main_service.event_comment.util;

public enum CommentState {
    NEW("NEW"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String state;

    CommentState(String state) {
        this.state = state;
    }

    public String toString() {
        return state;
    }
}