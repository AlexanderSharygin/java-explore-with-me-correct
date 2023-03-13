package ru.practicum.ewm.main_service.event_comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.main_service.event_comment.util.EventCommentAdminAction;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventCommentUpdateRequest {
    @Size(min = 5, max = 1000)
    private String text;
    private EventCommentAdminAction action;
}
