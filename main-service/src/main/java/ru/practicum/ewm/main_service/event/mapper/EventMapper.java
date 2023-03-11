package ru.practicum.ewm.main_service.event.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.dto.NewEventDto;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.util.EventState;
import ru.practicum.ewm.main_service.event_category.dto.EventCategoryDto;
import ru.practicum.ewm.main_service.event_category.model.EventCategory;
import ru.practicum.ewm.main_service.user.dto.UserShortDto;
import ru.practicum.ewm.main_service.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
public class EventMapper {

    public static EventFullDto fromEventToEventFullDto(Event event, EventCategoryDto eventCategoryDto, UserShortDto owner,
                                                       Long confirmedRequests, Integer views) {
        return new EventFullDto(event.getId(), event.getAnnotation(), eventCategoryDto, confirmedRequests,
                event.getCreatedDateTime(), event.getDescription(), event.getStartDateTime(), owner,
                event.getLocation(), event.isPaid(), event.getParticipantLimit(), event.getPublishedDateTime(),
                event.isNeededModeration(), event.getState(), event.getTitle(), views
        );
    }

    public static EventShortDto fromEventToEventShortDto(Event event, EventCategoryDto eventCategoryDto, UserShortDto owner,
                                                         Long confirmedRequests, Integer views) {
        return new EventShortDto(event.getId(), event.getAnnotation(), eventCategoryDto, confirmedRequests,
                event.getStartDateTime(), owner, event.isPaid(), event.getTitle(), views);
    }

    public static Event fromNewEventDtoToEvent(NewEventDto newEventDto, User owner, EventCategory category) {
        return new Event(null, newEventDto.getTitle(), newEventDto.getAnnotation(), newEventDto.getDescription(),
                category, LocalDateTime.now(), newEventDto.getEventDate(), owner, newEventDto.getLocation(),
                newEventDto.getPaid(), newEventDto.getParticipantLimit(), null,
                newEventDto.getRequestModeration(), EventState.PENDING);
    }
}
