package ru.practicum.ewm.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.main_service.event_category.dto.CategoryDto;
import ru.practicum.ewm.main_service.user.dto.UserShortDto;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventShortDto {
    private long id;
    private String annotation; //required
    private CategoryDto category; //required
    private long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate; //required
    private UserShortDto initiator; //required
    private Boolean paid; //required
    private String title; //required
    private Integer views;
}
