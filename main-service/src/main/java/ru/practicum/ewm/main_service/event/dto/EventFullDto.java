package ru.practicum.ewm.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.main_service.event_category.dto.EventCategoryDto;
import ru.practicum.ewm.main_service.user.dto.UserShortDto;
import ru.practicum.ewm.main_service.event.util.EventState;
import ru.practicum.ewm.main_service.location.model.Location;


import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventFullDto {
    private long id;
    private String annotation;
    private EventCategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title; //required
    private Integer views;

  /*  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventFullDto that = (EventFullDto) o;
        return id == that.id && Objects.equals(annotation, that.annotation) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(description, that.description) &&
                Objects.equals(eventDate, that.eventDate) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, createdOn, description, eventDate, title);
    }*/
}
