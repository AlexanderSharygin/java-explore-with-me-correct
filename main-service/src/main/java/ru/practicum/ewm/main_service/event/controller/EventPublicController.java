package ru.practicum.ewm.main_service.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.EventShortDto;
import ru.practicum.ewm.main_service.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class EventPublicController {
    private final EventService eventService;

    private final StatClient statClient;

    private final String app = "ewm-main";

    @Autowired
    public EventPublicController(EventService eventService, StatClient statClient) {
        this.eventService = eventService;
        this.statClient = statClient;
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEvents(@PathVariable Long id,
                                  HttpServletRequest request) {
        statClient.create(new HitDto(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));

        return eventService.getById(id);
    }

    @GetMapping("/events")
    public List<EventShortDto> getAllEvents(@RequestParam(value = "text", required = false) String text,
                                            @RequestParam(value = "categories", required = false) List<Long> categories,
                                            @RequestParam(value = "paid", defaultValue = "false") boolean paid,
                                            @RequestParam(value = "rangeStart", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(value = "rangeEnd", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(value = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(value = "sort", required = false) String sort,
                                            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                            @Positive @RequestParam(value = "size", defaultValue = "10") int size,
                                            HttpServletRequest request) {
        statClient.create(new HitDto(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));

        return eventService.getAllShort(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }
}