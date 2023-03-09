package ru.practicum.ewm.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.main_service.location.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 500)
    private String annotation;
    @NotNull
    private long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private long participantLimit;
    private Boolean requestModeration;
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;
}
