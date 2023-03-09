package ru.practicum.ewm.main_service.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.main_service.event.dto.*;
import ru.practicum.ewm.main_service.event.mapper.EventMapper;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.event.util.AdminEventAction;
import ru.practicum.ewm.main_service.event.util.EventState;
import ru.practicum.ewm.main_service.event_category.mapper.CategoryMapper;
import ru.practicum.ewm.main_service.event_category.model.EventCategory;
import ru.practicum.ewm.main_service.event_category.repository.CategoryRepository;
import ru.practicum.ewm.main_service.exception.model.BadRequestException;
import ru.practicum.ewm.main_service.exception.model.ConflictException;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;
import ru.practicum.ewm.main_service.location.model.Location;
import ru.practicum.ewm.main_service.location.service.LocationService;
import ru.practicum.ewm.main_service.participate_request.model.ParticipationRequest;
import ru.practicum.ewm.main_service.participate_request.repository.RequestRepository;
import ru.practicum.ewm.main_service.participate_request.util.RequestStatus;
import ru.practicum.ewm.main_service.user.mapper.UserMapper;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;
    private final LocationService locationService;

    @Autowired
    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository,
                        UserRepository userRepository, RequestRepository requestRepository,
                        StatClient statClient,
                        LocationService locationService) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.statClient = statClient;
        this.locationService = locationService;
    }

    public EventFullDto getById(Long eventId) {
        Event event = getEventIfExist(eventId);
        if (event.getPublishedDateTime() == null) {
            throw new NotFoundException("Event with id" + eventId + " not published yet");
        }

        return getEventFullDto(event);
    }

    public List<EventShortDto> getAllShort(String text, List<Long> categories, boolean paid,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, boolean onlyAvailable, String sort,
                                           int from, int size) {
        Page<Event> events;
        Pageable paging;
        if (sort == null) {
            paging = PageRequest.of(from, size);
        } else {
            if (sort.equals("VIEWS") || sort.equals("EVENT_DATE") || sort.isBlank()) {
                if (sort.equals("EVENT_DATE")) {
                    paging = PageRequest.of((from) % size, size, Sort.by("startDateTime").descending());
                } else {
                    paging = PageRequest.of(from, size);
                }
            } else {
                throw new ConflictException("Wrong sorting. Only VIEW or EVENT_DATE values could be used");
            }
        }
        if (onlyAvailable) {
            if (rangeStart == null || rangeEnd == null) {
                events = eventRepository.findAllAvailablePublishedEventsByCategoryAndStateAfterDate(text,
                        now(), categories, paging, EventState.PUBLISHED, RequestStatus.CONFIRMED, paid);
            } else {
                events = eventRepository.findAllAvailablePublishedEventsByCategoryAndStateBetweenDates(text, rangeStart,
                        rangeEnd, categories, paging, EventState.PUBLISHED, RequestStatus.CONFIRMED, paid);
            }
        } else {
            if (rangeStart == null || rangeEnd == null) {
                events = eventRepository.findAllEventsWithStatusAfterDate(text, now(), categories,
                        EventState.PUBLISHED, paging, paid);
            } else {
                events = eventRepository.findAllEventsWithStatusBetweenDates(text, rangeStart, rangeEnd, categories,
                        EventState.PUBLISHED, paging, paid);
            }
        }

        return getEventsShorts(events.stream().collect(Collectors.toList()));
    }

    public List<EventFullDto> getAll(List<Long> users, List<String> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        List<EventState> eventStates = new ArrayList<>();
        if (states != null) {
            for (String state : states) {
                eventStates.add(EventState.valueOf(state));
            }
        } else {
            eventStates = null;
        }

        if (states != null) {
            for (String state : states) {
                if (!state.equals(EventState.PUBLISHED.toString()) && !state.equals(EventState.CANCELED.toString()) &&
                        !state.equals(EventState.PENDING.toString())) {
                    throw new BadRequestException("Wrong status. Only PUBLISHED or PENDING or  CANCELED values could be used");
                }
            }
        }
        Page<Event> events;
        if (rangeStart == null || rangeEnd == null) {
            events = eventRepository.findAllEventsAfterDateForUsersByStateAndCategories(users, eventStates, categories,
                    now(), pageable);
        } else {
            events = eventRepository.findAllEventsBetweenDatesForUsersByStateAndCategories(users, eventStates,
                    categories, rangeStart, rangeEnd, pageable);
        }

        return getEventsFulls(events.stream().collect(Collectors.toList()));
    }

    public EventFullDto updateByUser(UpdateEventUserRequest eventDto, Long userId, Long eventId) {
        Event event = getEventIfExist(eventId);
        getUserIfExist(userId);

        if (!Objects.equals(event.getOwner().getId(), userId)) {
            throw new NotFoundException("User with id" + userId + " is not owner for event with id" + eventId);
        }

        if (!event.getState().equals(EventState.PENDING) && !event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        checkEventStartDate(eventDto.getEventDate());
        if (eventDto.getCategory() != null) {
            EventCategory category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Category with id " + eventDto.getCategory() + "not exist in the DB"));
            event.setCategory(category);
        }
        if (eventDto.getAnnotation() != null && !event.getAnnotation().isBlank()) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank()) {
            event.setDescription(eventDto.getDescription());
        }
        updateEvent(event, eventDto.getEventDate(), eventDto.getLocation(), eventDto.getPaid(),
                eventDto.getParticipantLimit(), eventDto.getRequestModeration());
        switch (eventDto.getStateAction()) {
            case CANCEL_REVIEW:
                event.setState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            default:
                throw new ConflictException("Unknown event state");
        }
        if (eventDto.getTitle() != null && !eventDto.getTitle().isBlank()) {
            event.setTitle(eventDto.getTitle());
        }

        return getEventFullDto(event);
    }

    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = getEventIfExist(eventId);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Only pending events can be published");
        }
        if (event.getPublishedDateTime() != null &&
                eventDto.getStateAction().equals(AdminEventAction.REJECT_EVENT)) {
            throw new ConflictException("Wrong status for the event. Only unpublished events can be rejected");
        }
        checkEventStartDate(eventDto.getEventDate());
        if (eventDto.getCategory() != null) {
            EventCategory category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Category with id " + eventDto.getCategory() + "not exist in the DB"));
            event.setCategory(category);
        }
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        updateEvent(event, eventDto.getEventDate(), eventDto.getLocation(), eventDto.getPaid(),
                eventDto.getParticipantLimit(), eventDto.getRequestModeration());
        switch (eventDto.getStateAction()) {
            case PUBLISH_EVENT:
                event.setState(EventState.PUBLISHED);
                event.setPublishedDateTime(now());
                break;
            case REJECT_EVENT:
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new ConflictException("Wrong event state value");
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        Location location = event.getLocation();
        event.setLocation(locationService.save(location));
        Event updatedEvent = eventRepository.save(event);

        return getEventFullDto(updatedEvent);
    }

    public List<EventShortDto> getByUserId(Long userId, Pageable paging) {
        User user = getUserIfExist(userId);
        List<Event> events = eventRepository.findAllByOwner(user, paging).stream().collect(Collectors.toList());

        return getEventsShorts(events);
    }

    public EventFullDto create(NewEventDto eventDto, Long userId) {
        User owner = getUserIfExist(userId);
        EventCategory category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        "Category with id " + eventDto.getCategory() + "not exist in the DB"));
        checkEventStartDate(eventDto.getEventDate());
        Event toSave = EventMapper.fromNewEventDtoToEvent(eventDto, owner, category);
        Location savedLocation = locationService.save(toSave.getLocation());
        toSave.setLocation(savedLocation);
        Event event = eventRepository.save(toSave);

        return EventMapper.fromEventToEventFullDto(event, CategoryMapper.toCategoryDtoFromCategory(category),
                UserMapper.fromUserToUserShortDto(owner), 0L, 0);
    }

    public EventFullDto getEventByUserId(Long userId, Long eventId) {
        Event event = getEventIfExist(eventId);
        getUserIfExist(userId);
        if (!Objects.equals(event.getOwner().getId(), userId)) {
            throw new NotFoundException("User with id" + userId + " is not owner for event with id" + eventId);
        }

        return getEventFullDto(event);
    }

    public Integer getEventsViews(Long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        List<HashMap<Object, Object>> stats = getStats(uris);
        if (stats != null && !stats.isEmpty()) {
            return (Integer) stats.get(0).get("hits");
        } else {
            return 0;
        }
    }

    public Map<Long, Integer> getEventsViewsMap(List<Long> eventsIds) {
        List<String> uris = new ArrayList<>();
        for (Long eventId : eventsIds) {
            uris.add("/events/" + eventId);
        }
        List<HashMap<Object, Object>> stats = getStats(uris);
        Map<Long, Integer> eventViewsMap = new HashMap<>();
        if (stats != null && !stats.isEmpty()) {
            for (var map : stats) {
                String uri = (String) map.get("uri");
                String[] urisAsArr = uri.split("/");
                Long id = Long.parseLong(urisAsArr[urisAsArr.length - 1]);
                eventViewsMap.put(id, (Integer) map.get("hits"));
            }
        }
        for (Long id : eventsIds) {
            if (!eventViewsMap.containsKey(id)) {
                eventViewsMap.put(id, 0);
            }
        }

        return eventViewsMap;
    }

    private List<EventShortDto> getEventsShorts(List<Event> events) {
        List<Long> eventIds = getEventsIdFromEventsList(events);
        Map<Long, Long> confirmedRequestsCountForEvents = getConfirmedRequestsCountForEvents(events);
        Map<Long, Integer> viewsMap = getEventsViewsMap(new ArrayList<>(eventIds));

        return events.stream()
                .map(event -> EventMapper.fromEventToEventShortDto(event,
                        CategoryMapper.toCategoryDtoFromCategory(event.getCategory()),
                        UserMapper.fromUserToUserShortDto(event.getOwner()),
                        confirmedRequestsCountForEvents.getOrDefault(event.getId(), 0L),
                        viewsMap.get(event.getId())))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsFulls(List<Event> events) {
        List<Long> eventIds = getEventsIdFromEventsList(events);
        Map<Long, Long> confirmedRequestsCountForEvents = getConfirmedRequestsCountForEvents(events);

        Map<Long, Integer> viewsMap = getEventsViewsMap(new ArrayList<>(eventIds));

        return events.stream()
                .map(event -> EventMapper.fromEventToEventFullDto(event,
                        CategoryMapper.toCategoryDtoFromCategory(event.getCategory()),
                        UserMapper.fromUserToUserShortDto(event.getOwner()),
                        confirmedRequestsCountForEvents.getOrDefault(event.getId(), 0L),
                        viewsMap.get(event.getId())))
                .collect(Collectors.toList());

    }

    private EventFullDto getEventFullDto(Event event) {
        Long confirmedRequests = (long) requestRepository
                .findAllByEventAndStatus(event, RequestStatus.CONFIRMED).size();
        Integer views = getEventsViews(event.getId());

        return EventMapper.fromEventToEventFullDto(event,
                CategoryMapper.toCategoryDtoFromCategory(event.getCategory()),
                UserMapper.fromUserToUserShortDto(event.getOwner()),
                confirmedRequests,
                views);
    }

    private void updateEvent(Event event, LocalDateTime eventDate, Location location, Boolean paid, Long
            participantLimit, Boolean requestModeration) {

        if (eventDate != null) {
            event.setStartDateTime(eventDate);
        }

        if (location != null) {
            event.setLocation(location);
        }

        if (paid != null) {
            event.setPaid(paid);
        }

        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }

        if (requestModeration != null) {
            event.setNeededModeration(requestModeration);
        }
    }

    private User getUserIfExist(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id" + userId + " not exists in the DB."));
    }

    private Event getEventIfExist(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id" + eventId + " not exist in the DB"));
    }

    private void checkEventStartDate(LocalDateTime startDateTime) {
        if (startDateTime != null &&
                startDateTime.minusHours(1).minusMinutes(59).isBefore(now())) {
            throw new ConflictException("Invalid eventStarDate: " + startDateTime +
                    "+. Event should be started at least before for 2 hours after now");
        }
    }

    private List<HashMap<Object, Object>> getStats(List<String> uris) {
        return (List<HashMap<Object, Object>>) statClient.getStats("2000-01-01 00:00:00",
                now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                uris, false).getBody();
    }

    private List<Long> getEventsIdFromEventsList(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getConfirmedRequestsCountForEvents(List<Event> events) {
        List<ParticipationRequest> requests = requestRepository.findAllByEventInAndStatus(new ArrayList<>(events),
                RequestStatus.CONFIRMED);
        Set<Long> requestsIds = new HashSet<>();
        for (var request : requests) {
            requestsIds.add(request.getEvent().getId());
        }
        Map<Long, Long> confirmedRequestsCountForEvents = new HashMap<>();
        for (var id : requestsIds) {
            int count = (int) requests.stream().filter(k -> Objects.equals(k.getEvent().getId(), id)).count();
            confirmedRequestsCountForEvents.put(id, (long) count);
        }
        return confirmedRequestsCountForEvents;
    }
}