package ru.practicum.ewm.main_service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;

import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
