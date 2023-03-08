package ru.practicum.ewm.main_service.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.event.util.EventState;
import ru.practicum.ewm.main_service.event_category.dto.CategoryDto;
import ru.practicum.ewm.main_service.user.dto.UserShortDto;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.dto.NewEventDto;
import ru.practicum.ewm.main_service.event_category.model.EventCategory;
import ru.practicum.ewm.main_service.event.model.Event;

import ru.practicum.ewm.main_service.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventFullDto toEventFullDto(Event event,
                                              CategoryDto categoryDto,
                                              UserShortDto initiator,
                                              Long confirmedRequests,
                                              Integer views) {

        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                categoryDto,
                confirmedRequests,
                event.getCreatedDateTime(),
                event.getDescription(),
                event.getStartDateTime(),
                initiator,
                event.getLocation(),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedDateTime(),
                event.isNeededModeration(),
                event.getState(),
                event.getTitle(),
                views
        );

    }


    public static EventShortDto toEventShortDto(Event event,
                                                CategoryDto categoryDto,
                                                UserShortDto initiator,
                                                Long confirmedRequests,
                                                Integer views) {

        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                categoryDto,
                confirmedRequests,
                event.getStartDateTime(),
                initiator,
                event.isPaid(),
                event.getTitle(),
                views);

    }

    public static Event toEvent(NewEventDto newEventDto, User initiator, EventCategory category) {
        return new Event(null, newEventDto.getTitle(), newEventDto.getAnnotation(), newEventDto.getDescription(),
                category, LocalDateTime.now(), newEventDto.getEventDate(), initiator, newEventDto.getLocation(),
                newEventDto.getPaid(), newEventDto.getParticipantLimit(), null, newEventDto.getRequestModeration(),
                EventState.PENDING);
    }

}
