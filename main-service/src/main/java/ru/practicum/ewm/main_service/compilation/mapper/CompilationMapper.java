package ru.practicum.ewm.main_service.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.compilation.dto.CompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.CompilationRequest;
import ru.practicum.ewm.main_service.compilation.model.Compilation;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.model.Event;

import java.util.Set;

@NoArgsConstructor
public class CompilationMapper {

    public static CompilationDto toDtoFromCompilation(Compilation compilation, Set<EventShortDto> eventShortDtoList) {

        return new CompilationDto(eventShortDtoList, compilation.getId(), compilation.isPinned(), compilation.getTitle());
    }

    public static Compilation toCompilationFromDto(CompilationRequest compilationDto, Set<Event> eventsList) {

        return new Compilation(null, eventsList, compilationDto.getPinned(), compilationDto.getTitle());
    }
}
