package ru.practicum.ewm.main_service.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.main_service.compilation.dto.CompilationDto;
import ru.practicum.ewm.main_service.compilation.dto.CompilationRequest;
import ru.practicum.ewm.main_service.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.main_service.compilation.model.Compilation;
import ru.practicum.ewm.main_service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.mapper.EventMapper;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.event.service.EventService;
import ru.practicum.ewm.main_service.event_category.mapper.CategoryMapper;
import ru.practicum.ewm.main_service.exception.model.BadRequestException;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;
import ru.practicum.ewm.main_service.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    private final StatClient statClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public CompilationService(CompilationRepository compilationRepository, EventRepository eventRepository,
                              EventService eventService, StatClient statClient) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.statClient = statClient;
    }

    public List<CompilationDto> getAll(boolean pinned, Pageable pageable) {
        List<Compilation> compilations = compilationRepository
                .getAllByPinned(pinned, pageable).stream().collect(Collectors.toList());
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            Set<EventShortDto> items = new HashSet<>();
            if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
                Set<Event> eventSet = compilation.getEvents();
                items = getEventsShorts(eventSet);
            }
            result.add(CompilationMapper.toDtoFromCompilation(compilation, items));
        }

        return result;
    }

    public CompilationDto getById(long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + id + " not exists in DB"));

        Set<EventShortDto> items = new HashSet<>();

        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            Set<Event> eventSet = compilation.getEvents();
            items = getEventsShorts(eventSet);
        }

        return CompilationMapper.toDtoFromCompilation(compilation, items);
    }

    public CompilationDto create(CompilationRequest compilationDto) {
        if (compilationDto.getTitle() == null || compilationDto.getTitle().isBlank() ||
                compilationDto.getTitle().length() > 128) {
            throw new BadRequestException("Title field should be populated");
        }
        Set<Long> eventIds = compilationDto.getEvents();
        Set<Event> eventSet = new HashSet<>();
        Set<EventShortDto> items = new HashSet<>();
        if (eventIds.size() > 0) {
            eventSet = new HashSet<>(eventRepository.findAllById(eventIds));
            if (eventIds.size() == eventSet.size()) {
                items = getEventsShorts(eventSet);
            } else {
                throw new NotFoundException("Some events not exists in the DB");
            }
        }
        Compilation compilationToSave = CompilationMapper.toCompilationFromDto(compilationDto, eventSet);
        Compilation compilation = compilationRepository.save(compilationToSave);

        return CompilationMapper.toDtoFromCompilation(compilation, items);
    }

    public void delete(long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compilationId + " not exists in DB"));
        compilationRepository.delete(compilation);
    }

    public CompilationDto updateCompilation(long compilationId, CompilationRequest updateCompilationRequest) {
        Compilation existedCompilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compilationId + " not exists in DB"));
        Set<Event> eventSet;
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            eventSet = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            if (updateCompilationRequest.getEvents().size() == eventSet.size()) {
                existedCompilation.setEvents(eventSet);
            } else {
                throw new NotFoundException("Some events not exists in the DB");
            }
        }
        eventSet = existedCompilation.getEvents();
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            existedCompilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            existedCompilation.setPinned(updateCompilationRequest.getPinned());
        }
        Set<EventShortDto> eventShortDto = getEventsShorts(eventSet);
        Compilation resultCompilation = compilationRepository.save(existedCompilation);

        return CompilationMapper.toDtoFromCompilation(resultCompilation, eventShortDto);
    }

    public Map<Long, Integer> getEventsViewsMap(List<Long> eventsIds) {
        List<String> uris = new ArrayList<>();
        for (Long eventId : eventsIds) {
            uris.add("/events/" + eventId);
        }
        List<HashMap<Object, Object>> stats = (List<HashMap<Object, Object>>) statClient.getStats("2000-01-01 00:00:00",
                LocalDateTime.now().format(formatter),
                uris, false).getBody();
        Map<Long, Integer> eventViewsMap = new HashMap<>();
        if (stats != null && !stats.isEmpty()) {
            stats.forEach(map -> {
                String uri = (String) map.get("uri");
                String[] urisAsArr = uri.split("/");
                Long id = Long.parseLong(urisAsArr[urisAsArr.length - 1]);
                eventViewsMap.put(id, (Integer) map.get("hits"));
            });
        }
        for (Long id : eventsIds) {
            if (!eventViewsMap.containsKey(id)) {
                eventViewsMap.put(id, 0);
            }
        }

        return eventViewsMap;
    }

    private Set<EventShortDto> getEventsShorts(Set<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> confirmedRequestsCountForEvents = eventService
                .getConfirmedRequestsCountForEvents(new ArrayList<>(events));
        Map<Long, Integer> viewsMap = getEventsViewsMap(new ArrayList<>(eventIds));

        return events.stream()
                .map(event -> EventMapper.fromEventToEventShortDto(event,
                        CategoryMapper.toCategoryDtoFromCategory(event.getCategory()),
                        UserMapper.fromUserToUserShortDto(event.getOwner()),
                        confirmedRequestsCountForEvents.getOrDefault(event.getId(), 0L),
                        viewsMap.get(event.getId())))
                .collect(Collectors.toSet());
    }
}
