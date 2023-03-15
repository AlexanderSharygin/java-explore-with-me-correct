package ru.practicum.ewm.main_service.event_comment.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentDto;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentPublicInfo;
import ru.practicum.ewm.main_service.event_comment.model.EventComment;
import ru.practicum.ewm.main_service.user.model.User;

@NoArgsConstructor
public class EventCommentMapper {

    public static EventComment toEventCommentFromShortDto(EventCommentDto eventCommentDto, Event event, User user) {
        return new EventComment(-1L, eventCommentDto.getText(), event, user, eventCommentDto.getCreatedOn(),
                eventCommentDto.getState(), eventCommentDto.getIsPublished(), eventCommentDto.getPublishedOn());
    }

    public static EventCommentDto toDtoFromEventComment(EventComment comment) {
        return new EventCommentDto(comment.getId(), comment.getCommentText(), comment.getEvent().getId(),
                comment.getUser().getId(), comment.getUser().getName(), comment.getState(), comment.getCreatedDateTime(),
                comment.getPublishedDateTime(), comment.getIsPublished());
    }

    public static EventCommentPublicInfo toPublicInfoFromEventComment(EventComment comment) {
        return new EventCommentPublicInfo(comment.getId(), comment.getCommentText(), comment.getUser().getName(), comment.getPublishedDateTime());
    }
}