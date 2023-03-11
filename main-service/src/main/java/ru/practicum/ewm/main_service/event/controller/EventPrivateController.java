package ru.practicum.ewm.main_service.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event.dto.*;
import ru.practicum.ewm.main_service.event.service.EventService;
import ru.practicum.ewm.main_service.participate_request.service.ParticipateRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class EventPrivateController {

    private final EventService eventService;
    private final ParticipateRequestService requestService;

    @Autowired
    public EventPrivateController(EventService eventService, ParticipateRequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                             @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(from, size);
        return eventService.getByUserId(userId, paging);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto newEventDto, @PathVariable Long userId) {
        return eventService.create(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvents(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByUserId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody UpdateEventUserRequest eventDto,
                                    @PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.updateByUser(eventDto, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@PathVariable Long userId, @PathVariable Long eventId,
                                                        @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.updateRequest(userId, eventId, request);
    }
}
