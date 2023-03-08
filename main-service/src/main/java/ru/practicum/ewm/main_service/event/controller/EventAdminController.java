package ru.practicum.ewm.main_service.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event.dto.EventFullDto;
import ru.practicum.ewm.main_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.main_service.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @Autowired
    public EventAdminController(EventService eventService) {
        this.eventService = eventService;
    }


    //events
    @GetMapping
    public List<EventFullDto> getAll(@RequestParam(value = "users", required = false) List<Long> users,
                                     @RequestParam(value = "states", required = false) List<String> states,
                                     @RequestParam(value = "categories", required = false) List<Long> categories,
                                     @RequestParam(value = "rangeStart", required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam(value = "rangeEnd", required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                     @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        return eventService.getAll(users, states, categories, rangeStart, rangeEnd, from, size);
    }


    @PatchMapping("/{eventId}")
    public EventFullDto publishEvent(@PathVariable Long eventId,
                                     @RequestBody UpdateEventAdminRequest eventDto) {
        return eventService.updateByAdmin(eventId, eventDto);
    }

}
